package org.hathitrust.htrc.data

object TextOptions extends Enumeration {
  type TextOptions = Value
  val TrimLines, RemoveEmptyLines, DehyphenateAtEol, ParaLines = Value
}