package com.example.records.ui.components.editor

class UndoRedoManager(initialBlocks: List<Block>) {
    private val undoStack = ArrayDeque<String>()
    private val redoStack = ArrayDeque<String>()
    private val maxHistory = 50

    var currentJson: String = initialBlocks.toJson()
        private set
        
    private var lastSnapshotTime: Long = 0
    private val debounceMs = 1000L

    // Called whenever blocks change via typing or normal editing
    fun onBlocksChanged(newBlocks: List<Block>) {
        val newJson = newBlocks.toJson()
        if (newJson == currentJson) return
        
        val now = System.currentTimeMillis()
        // If the user hasn't typed for `debounceMs`, we push the CURRENT state to undo stack
        // BEFORE it gets replaced by the new state. This groups rapid keystrokes.
        if (now - lastSnapshotTime > debounceMs) {
            undoStack.addLast(currentJson)
            if (undoStack.size > maxHistory) undoStack.removeFirst()
            redoStack.clear()
            lastSnapshotTime = now
        }
        currentJson = newJson
    }

    // Called on structural changes (e.g. adding/removing blocks, toggling checkboxes)
    fun forceSnapshot(oldBlocks: List<Block>) {
        val oldJson = oldBlocks.toJson()
        // Ensure we don't push duplicates
        if (undoStack.isEmpty() || undoStack.last() != oldJson) {
            undoStack.addLast(oldJson)
            if (undoStack.size > maxHistory) undoStack.removeFirst()
            redoStack.clear()
        }
        lastSnapshotTime = System.currentTimeMillis()
    }

    fun undo(): List<Block>? {
        if (undoStack.isEmpty()) return null
        val previousJson = undoStack.removeLast()
        redoStack.addLast(currentJson)
        currentJson = previousJson
        // Reset the timer so that immediate subsequent typing starts a new snapshot
        lastSnapshotTime = System.currentTimeMillis()
        return currentJson.toBlocks()
    }

    fun redo(): List<Block>? {
        if (redoStack.isEmpty()) return null
        val nextJson = redoStack.removeLast()
        undoStack.addLast(currentJson)
        currentJson = nextJson
        lastSnapshotTime = System.currentTimeMillis()
        return currentJson.toBlocks()
    }
    
    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()
}
