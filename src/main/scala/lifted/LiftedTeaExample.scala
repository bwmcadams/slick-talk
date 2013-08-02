package lifted

// Use H2Driver to connect to an H2 database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

object LiftedTeaExample extends App {

  // Definition of the VENDORS table
  object Vendors extends Table[(Int, String, String, String)]("VENDORS") {
    def id = column[Int]("VENDOR_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("VENDOR_NAME")
    def country = column[String]("VENDOR_COUNTRY")
    def url = column[String]("VENDOR_URL")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = id ~ name ~ country ~ url 
  }

  // Definition of the TEAS table
  object Teas extends Table[(String, Int, String, String, Double, String)]("TEAS") {
    def name = column[String]("TEA_NAME", O.PrimaryKey)
    def vendorID = column[Int]("VENDOR_ID")
    def kind = column[String]("TEA_KIND")
    def currency = column[String]("PRICE_CURRENCY")
    def price = column[Double]("PRICE")
    def size = column[String]("PACKAGE_SIZE")
    def * = name ~ vendorID ~ kind ~ currency ~ price ~ size
    // A reified foreign key relation that can be navigated to create a join
    def vendor = foreignKey("VENDOR_ FK", vendorID, Vendors)(_.id)
  }

  // Connect to the database and execute the following block within a session
  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
    // The session is never named explicitly. It is bound to the current
    // thread as the threadLocalSession that we imported

    // Create the tables, including primary and foreign keys
    (Vendors.ddl ++ Teas.ddl).create

    // Insert some suppliers
    Vendors.insert(1, "Stash", "USA", "http://stashtea.com")
    Vendors.insert(2, "Mariage Frères", "France", "http://mariagefreres.com")
    Vendors.insert(3, "Postcard Teas", "England", "http://postcardteas.com")
    Vendors.insert(4, "Silk Road Teas", "USA", "http://silkroadteas.com")
    Vendors.insert(5, "TeaGschwndner", "Germany", "http://shop.tgtea.com")
    Vendors.insert(6, "Seattle Teacup", "USA", "http://seattleteacup.com")
    Vendors.insert(7, "Le Palais De Thés", "France", "http://us.palaisdethes.com/en_us")


    // Insert some tea (using JDBC's batch insert feature, if supported by the DB)
    Teas.insertAll(
      ("Darjeeling Estate Golden Tipped", 1, "Black", "$", 15.00, "100g"),
      ("Irish Breakfast", 1, "Black", "$", 7.50, "100g"),
      ("China Keemun", 1, "Black", "$", 7.50, "100g"),
      ("Moroccan Mint Green Tea", 1, "Green", "$", 7.50, "100g"),
      ("White Tea from beyond the Skies™", 2, "White", "€", 105.00, "100g"),
      ("Blue Himalaya™", 2, "Oolong", "€", 28.00, "100g"),
      ("Golden Jamguri SFTGFOP1", 2, "Black", "€", 60.00, "100g"),
      ("Gianfranco's Earl Grey", 3, "Black", "£", 6.45, "50g"),
      ("Master Matsumoto's Supernatural Green", 3, "Green", "£", 11.95, "50g"),
      ("2012 Darjeeling Hilton DJ1 SFTPGFOP1", 7, "Black", "$", 56.00, "100g")
    )

    // Iterate through all coffees and output them
    println("Teas:")
    Query(Teas) foreach { case (name, vendorID, kind, currency, price, size) =>
      println(s"$name \t $vendorID \t $kind \t $currency$price \t $size")
    }

    // Why not let the database do the string conversion and concatenation?
    println("Teas (concatenated by DB):")
    val q1 = for(t <- Teas) // Teas lifted automatically to a Query
      yield ConstColumn("  ") ++ t.name ++ "\t" ++ t.vendorID.asColumnOf[String] ++
        "\t" ++ t.currency.asColumnOf[String] ++ t.price.asColumnOf[String] ++
        "\t" ++ t.size.asColumnOf[String]
    // The first string constant needs to be lifted manually to a ConstColumn
    // so that the proper ++ operator is found
    q1 foreach println

    // Perform a join to retrieve tea names and supplier names for
    // all the really good stuff (regardless of currency)
    println("Manual join:")
    val q2 = for {
      t <- Teas if t.price > 25.00
      v <- Vendors if v.id === t.vendorID
    } yield (t.name, v.name)
    for(r <- q2) println("  " + r._1 + " supplied by " + r._2)

    // Do the same thing using the navigable foreign key
    println("Join by foreign key:")
    val q3 = for {
      t <- Teas if t.price > 25.00
      v <- t.vendor
    } yield (t.name, v.name)
    // This time we read the result set into a List
    val l3: List[(String, String)] = q3.list
    for((r1, r2) <- l3) println("  " + r1 + " supplied by " + r2)

    // Check the SELECT statement for that query
    println(q3.selectStatement)

    // Compute the number of coffees by each supplier
    println("Teas per supplier:")
    val q4 = (for {
      t <- Teas
      v <- t.vendor
    } yield (t, v)).groupBy(_._2.id).map {
      case (_, q) => (q.map(_._2.name).min.get, q.length)
    }
    // .get is needed because SLICK cannot enforce statically that
    // the supplier is always available (being a non-nullable foreign key),
    // thus wrapping it in an Option
    q4 foreach { case (name, count) =>
      println("  " + name + ": " + count)
    }
  }
}
