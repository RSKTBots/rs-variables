package com.javatar.rs3

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import kraken.plugin.api.*
import java.io.File
import java.io.FileInputStream

class VarbitDefinition(
    val id: Int,
    val convarId: Int,
    val lsb: Int,
    val msb: Int
) {
    companion object {

        val varbits = mutableMapOf<Int, VarbitDefinition>()
        val varps = mutableMapOf<Int, MutableList<VarbitDefinition>>()

        fun getVar(def: VarbitDefinition): Int {
            val varp = Client.getConVarById(def.convarId)
            if (varp != null) {
                val bits = def.msb - def.lsb
                return varp.getValueMasked(def.lsb, if (bits == 0) 1 else bits)
            }
            return 0
        }

        fun getMaskedValue(varbitId: Int, value: Int): Int {
            val varbit = varbits[varbitId]
            if (varbit != null) {
                return value shr varbit.lsb and Bits.BIT_TABLE[varbit.msb - varbit.lsb]
            }
            return 0
        }

        fun loadDefinitions() {
            val stream = FileInputStream(
                File(
                    Kraken.getPluginDir(),
                    "varbits.json"
                )
            )
            val json = String(stream.readBytes())
            val defsArray = Json.decodeFromString<JsonArray>(json)
            defsArray.map { it.jsonObject }.forEach {
                val varbitId = it["id"]?.jsonPrimitive?.int ?: -1
                val convarId = it["index"]?.jsonPrimitive?.int ?: -1
                val lsb = it["least_significant_bit"]?.jsonPrimitive?.int ?: -1
                val msb = it["most_significant_bit"]?.jsonPrimitive?.int ?: -1
                val def = VarbitDefinition(varbitId, convarId, lsb, msb)
                varbits[varbitId] = def
                val list = varps.getOrPut(convarId) { mutableListOf() }
                list.add(def)
            }
            Debug.log("Loaded ${varbits.size} Varbits.")
        }
    }
}