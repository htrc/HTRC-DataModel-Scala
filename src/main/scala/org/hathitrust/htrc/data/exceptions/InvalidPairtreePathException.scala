package org.hathitrust.htrc.data.exceptions

@SuppressWarnings(Array("org.wartremover.warts.Null"))
case class InvalidPairtreePathException(path: String, message: String = null, cause: Throwable = null)
  extends Exception(s"$path" + Option(message).map(msg => s": $msg").getOrElse(""), cause)