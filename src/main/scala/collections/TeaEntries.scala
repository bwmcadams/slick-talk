/**
 * Copyright (c) 2011-2013 Brendan W. McAdams <http://evilmonkeylabs.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package collections

import java.net.URI


object TeaEntries {
  // Suppliers
  val Stash = Supplier("Stash", "USA", new URI("http://stashtea.com"))
  val Mariage = Supplier("Mariage Frères", "France", new URI("http://mariagefreres.com"))
  val Postcard = Supplier("Postcard Teas", "England", new URI("http://postcardteas.com"))
  val SilkRoad = Supplier("Silk Road Teas", "USA", new URI("http://silkroadteas.com"))
  val TGTea = Supplier("TeaGschwndner", "Germany", new URI("http://shop.tgtea.com"))
  val SeattleTeaCup = Supplier("Seattle Teacup", "USA", new URI("http://seattleteacup.com"))
  val Palais = Supplier("Le Palais De Thés", "France", new URI("http://us.palaisdethes.com/en_us"))

  // Stash
  val Darjeeling = Tea(Stash, "Darjeeling Estate Golden Tipped", TeaTypes.Black, "100g", '$', 15.00)
  val IrishBreakfast = Tea(Stash, "Irish Breakfast", TeaTypes.Black, "100g", '$', 7.50)
  val ChinaKeemun = Tea(Stash, "China Keemun", TeaTypes.Black, "100g", '$', 7.50)
  val MoroccanMint = Tea(Stash, "Moroccan Mint Green Tea", TeaTypes.Green, "100g", '$', 7.50)

  // Mariage Frères
  val BeyondSkies = Tea(Mariage, "White Tea from beyond the Skies™", TeaTypes.White, "100g", '€', 105.00)
  val BlueHimalaya = Tea(Mariage, "Blue Himalaya™", TeaTypes.Oolong, "100g", '€', 28)
  val GoldenJamguri = Tea(Mariage, "Golden Jamguri SFTGFOP1", TeaTypes.Black, "100g", '€', 60)

  // Postcard
  val EarlGrey = Tea(Postcard, "Gianfranco's Earl Grey", TeaTypes.Black, "50g", '£', 6.45)
  val SuperGreen = Tea(Postcard, "Master Matsumoto's Supernatural Green", TeaTypes.Green, "50g", '£', 11.95)

  // Le Palais De Thés
  val DarjeelingHilton = Tea(Palais, "2012 DARJEELING HILTON DJ1 S.F.T.P.G.F.O.P.1", TeaTypes.Black, "100g", '$', 56.00)


  val tea = Seq(Darjeeling, IrishBreakfast, ChinaKeemun, MoroccanMint, BeyondSkies, BlueHimalaya,
                   GoldenJamguri,  EarlGrey, SuperGreen, DarjeelingHilton)

  val tea2 = Seq[Tea](Darjeeling, IrishBreakfast, ChinaKeemun, MoroccanMint, BeyondSkies, BlueHimalaya,
                      GoldenJamguri,  EarlGrey, SuperGreen, DarjeelingHilton)

  val tea3: Seq[Tea] = Seq(Darjeeling, IrishBreakfast, ChinaKeemun, MoroccanMint, BeyondSkies, BlueHimalaya,
                           GoldenJamguri,  EarlGrey, SuperGreen, DarjeelingHilton)


  // this is a method - well defined, and we can call it normally
  def costsDollars(t: Tea): Boolean = t.currency == '$'

  // this is a function - I just happen to have captured it in a variable
  val costsEuros = (t: Tea) => t.currency == '€'

  /*def filter(p: (Tea) => Boolean): Seq[Tea]

  def groupBy[K](f: (Tea) => K): Map[K, Seq[Tea]]*/

  // pass a function we write inline
  val inEuros = tea.filter(t => t.currency == '€')

  // scala will also "lift" a method into a function when it needs to
  val inDollars = tea.filter(costsDollars)

  val typesOfTea = tea.groupBy(t => t.kind)

  val typesOfTeaStr = tea.groupBy(t => t.kind.toString)

  TeaEntries.tea.groupBy(t => t.kind.toString).keys


  val prices = tea.map(t => "%c%3.2f per %s".format(t.currency, t.price, t.size))

     //def flatMap[B](f: (Tea) => Traversable[B]): Seq[B]

}
