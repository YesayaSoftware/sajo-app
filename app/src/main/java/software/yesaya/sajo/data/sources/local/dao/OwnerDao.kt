package software.yesaya.sajo.data.sources.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import software.yesaya.sajo.data.sources.local.entities.Owner

/**
 * Data Access Object for the owner table.
 */
@Dao
interface OwnerDao {
    /**
     * Observe list of owners.
     *
     * @return all owners.
     */
    @Query("SELECT * FROM Owners")
    fun observeOwners() : LiveData<List<Owner>>

    /**
     * Observe a single Owner.
     *
     * @param ownerId the owner id.
     * @return the owner with ownerId
     */
    @Query("SELECT * FROM Owners WHERE id = :ownerId")
    fun observeOwnerById(ownerId : Int) : LiveData<Owner>

    /**
     * Select all owners from the owners table
     *
     * @return all owners.
     */
    @Query("SELECT * FROM Owners")
    suspend fun getOwners() : List<Owner>

    /**
     * Select owner by id.
     *
     * @param ownerId the owner id.
     * @return the owner with ownerId.
     */
    @Query("SELECT * FROM owners WHERE id = :ownerId")
    suspend fun getOwnerById(ownerId: Int) : Owner?

    /**
     * Insert owner in the database. If owner already exists, replace it.
     *
     * @param owner the owner to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(owner : Owner)

    /**
     * Update owner
     *
     * @param owner owner to be updated
     * @return the number of owners updated. This should always be 1.
     */
    @Update
    suspend fun updateOwner(owner : Owner) : Int

    /**
     * Delete a owner by id.
     *
     * @param ownerId the owner id.
     * @return the number of owners deleted. This should always be 1.
     */
    @Query("DELETE FROM Owners WHERE id = :ownerId")
    suspend fun deleteTaskById(ownerId: Int): Int

    /**
     * Delete all owners.
     */
    @Query("DELETE FROM Owners")
    suspend fun deleteOwners()

}