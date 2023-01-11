package com.github.manu.dungeonintroubles.extension

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.Fixture
import com.github.quillraven.fleks.Entity


fun Fixture.isStaticBody() = this.body.type == StaticBody

fun Fixture.isDinamicBody() = this.body.type == DynamicBody

val Fixture.entity: Entity
    get() = this.body.userData as Entity