package com.github.giedomak.telepathdb.costmodel

import com.github.giedomak.telepathdb.datamodels.ParseTree

object SimpleCostModel : CostModel {

    /**
     * Our SimpleCostModel will just return the height/level op the [tree] as the cost.
     */
    override fun cost(tree: ParseTree): Int {
        return tree.level()
    }
}
