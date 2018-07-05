package org.hathitrust.htrc.data.ops

object TextOptions extends Enumeration {
  type TextOptions = Value
  val TrimLines, RemoveEmptyLines, DehyphenateAtEol, ParaLines = Value
}