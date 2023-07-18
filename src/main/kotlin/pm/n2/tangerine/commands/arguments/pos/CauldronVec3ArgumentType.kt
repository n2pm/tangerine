package pm.n2.tangerine.commands.arguments.pos

import com.adryd.cauldron.api.command.CauldronClientCommandSource
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.CommandSource.RelativePosition
import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture

class CauldronVec3ArgumentType(private val centerIntegers: Boolean) : ArgumentType<CauldronPosArgument?> {
    override fun parse(stringReader: StringReader): CauldronPosArgument {
        return if (stringReader.canRead() && stringReader.peek() == '^') {
            CauldronLookingPosArgument.parse(stringReader)
        } else CauldronDefaultPosArgument.parse(stringReader, centerIntegers)
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        if (context.source is CommandSource) {
            val string = builder.remaining
            val collection =
                if (string.isNotEmpty() && string[0] == '^') setOf(RelativePosition.ZERO_LOCAL) else (context.source as CommandSource).positionSuggestions
            return CommandSource.suggestPositions(
                string,
                collection,
                builder,
                CommandManager.getCommandValidator(this::parse)
            )
        }
        return Suggestions.empty()
    }

    override fun getExamples(): Collection<String> = EXAMPLES

    companion object {
        private val EXAMPLES: Collection<String> =
            mutableListOf("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5")

        fun vec3() = CauldronVec3ArgumentType(true)

        fun getVec3(context: CommandContext<CauldronClientCommandSource?>, name: String?) =
            context.getArgument(name, CauldronPosArgument::class.java).toAbsolutePos(context.source!!)
    }
}

