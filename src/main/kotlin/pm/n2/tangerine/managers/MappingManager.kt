package pm.n2.tangerine.managers

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.mapping.tree.ClassDef
import net.fabricmc.mapping.tree.FieldDef
import net.fabricmc.mapping.tree.TinyMappingFactory
import net.fabricmc.mapping.tree.TinyTree
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
    lateinit var tree: TinyTree

    override fun init() {
        val stream = Tangerine::class.java.getResourceAsStream("/assets/tangerine/mappings.tiny") ?: return
        val reader = BufferedReader(stream.reader())
        tree = TinyMappingFactory.loadWithDetection(reader)
    }

    private fun getClass(clazz: Class<*>): ClassDef? {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) return null
        val name = clazz.name.replace(".", "/")
        return tree.defaultNamespaceClassMap[name]
    }

    private fun getField(clazz: Class<*>, field: Field): FieldDef? {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) return null
        val classDef = getClass(clazz) ?: return null

        return classDef.fields.firstOrNull {
            it.getName("intermediary") == field.name
        }
    }

    fun mapClass(clazz: Class<*>): String = getClass(clazz)
        ?.getName("named")
        ?.replace("/", ".")
        ?: clazz.name

    fun mapClassSimple(clazz: Class<*>): String = getClass(clazz)
        ?.getName("named")
        .let {
            it?.substringAfterLast("/")?.substringAfterLast("$")
                ?: clazz.simpleName
        }

    fun mapField(clazz: Class<*>, field: Field): String {
        // fucking `extends` go fuck yourself
        var superclazz: Class<*>? = clazz
        while (superclazz != null) {
            val name = getField(superclazz, field)?.getName("named")
            if (name != null) return name
            superclazz = superclazz.superclass
        }

        return field.name
    }
}
