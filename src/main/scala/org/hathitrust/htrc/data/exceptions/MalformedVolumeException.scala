package org.hathitrust.htrc.data.exceptions

@SuppressWarnings(Array("org.wartremover.warts.Null"))
case class MalformedVolumeException(msg: String, cause: Throwable = null) extends Exception(msg, cause)