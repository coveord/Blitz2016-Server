package com.coveo.blitz.server

case class Pos(x: Int, y: Int) {

  def north = copy(x = x - 1)
  def south = copy(x = x + 1)
  def east = copy(y = y + 1)
  def west = copy(y = y - 1)

  //def north = copy(y = y - 1)
  //def south = copy(y = y + 1)
  //def east = copy(x = x + 1)
  //def west = copy(x = x - 1)


  def neighbors = List(north, east, south, west)
  def neighborSet = neighbors.toSet

  val closeTo = neighborSet contains _

  def to(dir: Dir) = dir match {
    case Dir.North => north
    case Dir.South => south
    case Dir.East  => east
    case Dir.West  => west
    case _         => this
  }

  def isIn(size: Int) = (x >= 0 && x < size && y >= 0 && y < size)
}

sealed trait Dir
case object Dir {
  case object Stay extends Dir
  case object North extends Dir
  case object South extends Dir
  case object East extends Dir
  case object West extends Dir
  case object Crash extends Dir

  def apply(str: String): Dir = str.toLowerCase.trim match {
    case "north" => North
    case "south" => South
    case "east"  => East
    case "west"  => West
    case _       => Stay
  }
}

sealed abstract class Tile(char1: Char, char2: Char) {
  def render = char1.toString + char2.toString
}
object Tile {
  case object Air extends Tile(' ', ' ')
  case object Wall extends Tile('#', '#')
  case object Spikes extends Tile('^','^')
  case object Tavern extends Tile('[', ']')
  case class Mine(owner: Option[Int]) extends Tile('$', owner.fold('-')(_.toString.head))

  def read(str: String): Tile =
    if (str == "  ") Tile.Air
    else if (str == "^^") Tile.Spikes
    else if (str == "##") Tile.Wall
    else if (str == "[]") Tile.Tavern
    else if (str.head == '$') Tile.Mine(charInt(str(1)))
    else sys error s"Can't parse tile $str"
}

sealed trait Status {
  def finished = false
}
object Status {
  sealed trait Finish extends Status {
    override def finished = true
  }
  case object Created extends Status
  case object Started extends Status
  case object AllCrashed extends Finish
  case object TurnMax extends Finish
  case object Aborted extends Finish
}

case class Driver(play: Game => String)
object Driver {
  val Immobile = Driver(_ => "stay")
  val Random = Driver { _ =>
    scala.util.Random.shuffle(List("north", "south", "east", "west")).head
  }
}
