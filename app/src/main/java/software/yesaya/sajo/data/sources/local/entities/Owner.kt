package software.yesaya.sajo.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Immutable model class for a Owner. In order to compile with Room,
 * we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param name       name of the owner
 * @param id          id of the owner
 */

@Entity(tableName = "owners")
data class Owner @JvmOverloads constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "name") var name : String = ""
) { }