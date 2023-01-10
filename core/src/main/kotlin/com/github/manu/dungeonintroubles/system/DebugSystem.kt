package com.github.manu.dungeonintroubles.system

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.Qualifier
import ktx.assets.disposeSafely

class DebugSystem(
    private val physicWorld: World,
    @Qualifier("gameStage") private val gameStage: Stage
) : IntervalSystem(enabled = true) {

    private lateinit var physicsRenderer: Box2DDebugRenderer
//    private lateinit var shapeRenderer: ShapeRenderer

    init {
        if (enabled) {
            physicsRenderer = Box2DDebugRenderer()
//            shapeRenderer = ShapeRenderer()
        }
    }

    override fun onTick() {
        physicsRenderer.render(physicWorld, gameStage.camera.combined)
//        shapeRenderer.use(ShapeType.Line, gameStage.camera.combined) {
//            it.setColor(1f, 0f, 0f, 0f)
//            it.rect(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width - AABB_RECT.x, AABB_RECT.height - AABB_RECT.y)
//        }
    }

    override fun onDispose() {
        if (enabled) {
            physicsRenderer.disposeSafely()
//            shapeRenderer.disposeSafely()
        }
    }
}