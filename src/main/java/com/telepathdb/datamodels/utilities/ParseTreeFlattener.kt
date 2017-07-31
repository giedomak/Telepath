/**
 * Copyright (C) 2016-2017 - All rights reserved.
 * This file is part of the telepathdb project which is released under the GPLv3 license.
 * See file LICENSE.txt or go to http://www.gnu.org/licenses/gpl.txt for full license details.
 * You may use, distribute and modify this code under the terms of the GPLv3 license.
 */

package com.telepathdb.datamodels.utilities

import com.telepathdb.datamodels.ParseTree

object ParseTreeFlattener {

    fun flatten(parseTree: ParseTree): ParseTree {

        if (parseTree.isLeaf) return parseTree

        var offset = 0

        for (index in parseTree.children!!.indices) {

            val child = parseTree.children!![index + offset]
            val flattenedChild = flatten(child)

            if (child.operatorId == parseTree.operatorId) {

                parseTree.children!!.removeAt(index + offset)
                parseTree.children!!.addAll(index + offset, flattenedChild.children!!)

                offset += flattenedChild.children!!.size - 1
            }
        }

        return parseTree
    }
}
