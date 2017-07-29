package com.telepathdb.costmodel

import com.telepathdb.datamodels.ParseTree

object SimpleCostModel : CostModel {

    /**
     * Our SimpleCostModel will just return the height/level op the [tree] as the cost.
     */
    override fun cost(tree: ParseTree): Int {
        return tree.level()
    }
}
