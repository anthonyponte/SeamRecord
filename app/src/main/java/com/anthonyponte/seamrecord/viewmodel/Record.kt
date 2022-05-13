package com.anthonyponte.seamrecord.viewmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.idanatz.oneadapter.external.interfaces.Diffable
import java.io.Serializable
import java.time.Instant
import java.time.temporal.ChronoField

@Entity(tableName = "records")
data class Record(
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Instant,
    @ColumnInfo(name = "espesor_cuerpo") val espesorCuerpo: Double,
    @ColumnInfo(name = "espesor_tapa") val espesorTapa: Double,
    @ColumnInfo(name = "gancho_cuerpo") val ganchoCuerpo: Double,
    @ColumnInfo(name = "gancho_tapa") val ganchoTapa: Double,
    @ColumnInfo(name = "altura_cierre") val alturaCierre: Double,
    @ColumnInfo(name = "espesor_cierre") val espesorCierre: Double,
    @ColumnInfo(name = "traslape") var traslape: Double,
    @ColumnInfo(name = "superposicion") var superposicion: Double,
    @ColumnInfo(name = "penetracion") var penetracion: Double,
    @ColumnInfo(name = "espacio_libre") var espacioLibre: Double,
    @ColumnInfo(name = "compacidad") var compacidad: Double
) : Diffable, Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L

    override var uniqueIdentifier: Long = fechaCreacion.getLong(ChronoField.NANO_OF_SECOND)
    override fun areContentTheSame(other: Any): Boolean =
        other is Record && fechaCreacion == other.fechaCreacion
}
