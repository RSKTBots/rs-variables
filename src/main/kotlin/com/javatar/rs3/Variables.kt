package com.javatar.api.rs.vars

import com.javatar.rs3.VarbitDefinition
import com.javatar.rs3.VarbitDefinition.Companion.getVar
import kotlin.reflect.KProperty

object Variables {

    val definitions = mutableMapOf<String, VariableDefinition>()

    fun define(name: String, varbitId: Int) {
        definitions.putIfAbsent(name, VariableDefinition(name, varbitId))
    }

    fun byId(varbitId: Int) : Int {
        val vdef = VarbitDefinition.varbits[varbitId]
        if(vdef != null) {
            return getVar(vdef)
        }
        return 0
    }

    fun byName(name: String) : Int {
        val def = definitions[name]
        if (def != null) {
            val vdef = VarbitDefinition.varbits[def.varbitId]
            if (vdef != null) {
                return getVar(vdef)
            }
        }
        return 0
    }

    operator fun getValue(any: Any?, prop: KProperty<*>): Int {
        return byName(prop.name)
    }

}