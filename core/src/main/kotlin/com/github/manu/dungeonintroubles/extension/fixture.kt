package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.Fixture
import com.github.quillraven.fleks.Entity

/**
 * Extension de la clase Fixture para conversion del userData a una entidad del sistema
 */
val Fixture.entity: Entity
    get() = this.body.userData as Entity