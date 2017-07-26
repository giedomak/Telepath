/**
 * Copyright (C) 2017-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels

import java.io.Serializable
import java.util.*

/**
 * Node model
 * https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Node.java
 */
data class Node(val id: Long) : Serializable
