package com.github.manu.dungeonintroubles.ui.model

import kotlin.reflect.KProperty

/**
 * Clase que guarda y notifica a las clases que esuchan los cambios de distintas variables
 */
abstract class PropertyChangeSource {
    /**
     * Mapa que guarda las clases a la esucha de cambios de variables
     */
    @PublishedApi
    internal val listenersMap = mutableMapOf<KProperty<*>, MutableList<(Any) -> Unit>>()

    /**
     * Guard las funciones de las clases que escuchan para ejecutar codigo al ambiar la variable
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> onPropertyChange(property: KProperty<T>, noinline action: (T) -> Unit) {
        val actions = listenersMap.getOrPut(property) { mutableListOf() } as MutableList<(T) -> Unit>
        actions += action
    }

    /**
     * Notifica a las las clases del cambio en la variable
     */
    fun notify(property: KProperty<*>, value: Any) {
        listenersMap[property]?.forEach { it(value) }
    }
}

/**
 * Clase que guarda la variable que se va notificar y detecta sus cambios
 */
class PropertyNotifier<T : Any>(initialValue: T) {
    /**
     * Variable que se detecta el cambio
     */
    private var _value: T = initialValue

    /**
     * Devuelve el valor _value
     *
     * @param thisRef Referencia de la clase que notifica los cambios
     * @param property Tipo de propiedad que se quiere devolver
     *
     * @return _value
     */
    operator fun getValue(thisRef: PropertyChangeSource, property: KProperty<*>): T = _value

    /**
     * Guarda nueva información de la variable y notifica el cambio
     * @param thisRef Referencia de la clase que notifica los cambios
     * @param property Tipo de propiedad que se quiere guardar
     */
    operator fun setValue(thisRef: PropertyChangeSource, property: KProperty<*>, value: T) {
        _value = value
        thisRef.notify(property, value)
    }
}

/**
 * Clase para inicializar variables y poder notificar su cambios
 */
inline fun <reified T : Any> propertyNotify(initialValue: T): PropertyNotifier<T> = PropertyNotifier(initialValue)

