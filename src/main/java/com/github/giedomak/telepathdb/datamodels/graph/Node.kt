/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.github.giedomak.telepathdb.datamodels.graph

import java.io.Serializable

/**
 * Data class for our Node model.
 *
 * See https://github.com/maxsumrall/PathDB/blob/master/src/main/java/com/pathdb/pathIndex/Node.java for the
 * implementation in PathDB.
 */
data class Node(val id: Long) : Serializable
