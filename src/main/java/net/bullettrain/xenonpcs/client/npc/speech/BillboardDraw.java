package net.bullettrain.xenonpcs.client.npc.speech;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

/**
 * The two primitives every billboarded bubble is built from.
 *
 * <p>Both were private inside {@link SpeechBubbleRenderer} until dialogue options needed the same
 * two. Shared rather than copied: a second copy of the outlined-text pass would drift from this one
 * the first time either was touched, and they would stop looking like the same UI.
 *
 * <p>Provenance is worth keeping. {@link #texturedQuad} is a rewrite of DragonMineZ's
 * {@code RenderBufferUtil#drawTexturedQuad}, and {@link #outlinedLine} is the technique from its
 * {@code KiSenseEvent#drawText} - four dark passes offset by a pixel, then one in the main colour.
 * Both were read out of the decompiled jar, not guessed at.
 */
public final class BillboardDraw {

    /** Full-bright, so a bubble is readable in a cave at night. */
    private static final int LIGHT = 0xF000F0;

    private BillboardDraw() {
    }

    /** One textured quad in the billboard's local space. */
    public static void texturedQuad(Matrix4f matrix, float x0, float y0, float x1, float y1,
                                    float z, float minU, float minV, float maxU, float maxV) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x0, y1, z).uv(minU, maxV).endVertex();
        buffer.vertex(matrix, x1, y1, z).uv(maxU, maxV).endVertex();
        buffer.vertex(matrix, x1, y0, z).uv(maxU, minV).endVertex();
        buffer.vertex(matrix, x0, y0, z).uv(minU, minV).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    /** The whole sprite, which is what every bubble wants. */
    public static void texturedQuad(Matrix4f matrix, float x0, float y0, float x1, float y1) {
        texturedQuad(matrix, x0, y0, x1, y1, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    /**
     * One line of outlined text, centred on {@code centerX}.
     *
     * <p>Takes a {@link FormattedCharSequence} rather than a {@code String} because the caller has
     * already wrapped it - measuring and drawing a whole message as one line is what used to make
     * long text overflow its bubble.
     */
    public static void outlinedLine(Matrix4f matrix, FormattedCharSequence line, float lineWidth,
                                    float centerX, float top, float alpha,
                                    MultiBufferSource.BufferSource buffers) {
        Minecraft mc = Minecraft.getInstance();
        float x = centerX - lineWidth / 2.0f;

        int a = Math.round(Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f);
        int textColor = a << 24 | 0xFFFFFF;
        int outlineColor = a << 24;
        FormattedCharSequence outline = forceColor(line, 0x000000);

        mc.font.drawInBatch(outline, x + 1, top, outlineColor, false, matrix, buffers,
                Font.DisplayMode.SEE_THROUGH, 0, LIGHT);
        mc.font.drawInBatch(outline, x - 1, top, outlineColor, false, matrix, buffers,
                Font.DisplayMode.SEE_THROUGH, 0, LIGHT);
        mc.font.drawInBatch(outline, x, top + 1, outlineColor, false, matrix, buffers,
                Font.DisplayMode.SEE_THROUGH, 0, LIGHT);
        mc.font.drawInBatch(outline, x, top - 1, outlineColor, false, matrix, buffers,
                Font.DisplayMode.SEE_THROUGH, 0, LIGHT);
        buffers.endBatch();
        mc.font.drawInBatch(line, x, top, textColor, false, matrix, buffers,
                Font.DisplayMode.SEE_THROUGH, 0, LIGHT);
        buffers.endBatch();
    }

    /** Preserves formatting flags while making every formatted run use the same outline colour. */
    static FormattedCharSequence forceColor(FormattedCharSequence text, int color) {
        return sink -> text.accept((index, style, codePoint) ->
                sink.accept(index, style.withColor(color), codePoint));
    }
}
