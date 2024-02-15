package pm.n2.tangerine.managers

import net.fabricmc.loader.api.FabricLoader
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.core.Manager
import java.io.BufferedReader
import java.lang.reflect.Field

/*
 * For things like the packet logger, we need to obtain the names of classes and fields, even when not in the
 * development environment. The (bad) solution to this is to bundle Yarn mappings within Tangerine, which is
 * about a megabyte zipped.
 *
 * IDEA is lying about errors and nullability here - shit builds even with them, I dunno how to suppress it,
 * just ignore em.
 */
object MappingManager : Manager {
    // Mappings got removed I guess, just do this for now
    fun mapClass(clazz: Class<*>) = clazz.name
    fun mapClassSimple(clazz: Class<*>) = mapClass(clazz)
    fun mapField(clazz: Class<*>, field: Field) = field.name
}
