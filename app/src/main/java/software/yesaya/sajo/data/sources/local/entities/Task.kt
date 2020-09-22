package software.yesaya.sajo.data.sources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import software.yesaya.sajo.utils.smartTruncate

/**
 * Immutable model class for a Task. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param title       title of the task
 * @param description description of the task
 * @param completed   whether or not this task is completed
 * @param id          id of the task
 */
@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "completed") var completed: Boolean = false,
    @ColumnInfo(name = "created_at") var created_at: String,
    @ColumnInfo(name = "updated_at") var updated_at: String?,
    @Embedded(prefix = "owner_") var owner : Owner?
) {
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !completed

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()

    val shortDescription: String
        get() = description.smartTruncate(200)
}