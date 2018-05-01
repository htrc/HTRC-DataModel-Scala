package org.hathitrust.htrc.data

object TextOptions extends Enumeration {
  type TextOptions = Value
  val TrimLines, RemoveEmptyLines, FixHyphenation, ParaLines = Value
}