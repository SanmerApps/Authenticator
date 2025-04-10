package dev.sanmer.authenticator.ktx

class HashBiMap<K, V> {
    private val forwardMap = HashMap<K, V>()
    private val backwardMap = HashMap<V, K>()

    fun put(key: K, value: V): V? {
        val oldValue = forwardMap[key]
        if (oldValue != null) {
            backwardMap.remove(oldValue)
        }

        val oldKey = backwardMap[value]
        if (oldKey != null) {
            forwardMap.remove(oldKey)
        }

        forwardMap[key] = value
        backwardMap[value] = key
        return oldValue
    }

    fun get(key: K): V? = forwardMap[key]

    fun getKey(value: V): K? = backwardMap[value]

    fun removeByKey(key: K): V? {
        val value = forwardMap.remove(key)
        if (value != null) {
            backwardMap.remove(value)
        }
        return value
    }

    fun removeByValue(value: V): K? {
        val key = backwardMap.remove(value)
        if (key != null) {
            forwardMap.remove(key)
        }
        return key
    }

    fun containsKey(key: K): Boolean = forwardMap.containsKey(key)

    fun containsValue(value: V): Boolean = backwardMap.containsKey(value)

    fun size(): Int = forwardMap.size

    fun clear() {
        forwardMap.clear()
        backwardMap.clear()
    }

    fun keys(): Set<K> = forwardMap.keys
    fun values(): Set<V> = backwardMap.keys
}
