# 📐 Snappy Ruler Set (Android)

Snappy Ruler Set is a lightweight **Android drawing app** that combines **freehand sketching** with **virtual geometry tools** (Ruler, Set Squares, Protractor, optional Compass).  
The tools support **magnetic snapping** to grid, endpoints, midpoints, intersections, and common angles — making it quick and accurate to construct clean diagrams.  

---

## 🚀 Overview

- **Platform:** Android (Kotlin, Jetpack Compose or Views + Canvas hybrid)  
- **Target performance:** 60 FPS on mid-range device (6 GB RAM)  
- **Accuracy:**  
  - Angle readouts within ±0.5°  
  - Length display in cm with 1 mm granularity (calibration via DPI or manual adjustment)  
- **User Experience:**  
  - Snappy, magnetic feel with subtle haptics and HUD overlays  
  - Undo/Redo (≥20 steps)  
  - Export as PNG/JPEG  

---

## 🛠️ Tech Stack & Libraries

- **Language:** Kotlin  
- **UI Layer:** Jetpack Compose (preferred) + Android Canvas for precision drawing  
- **Gesture Handling:**  
  - `pointerInput` with `detectTransformGestures` (Compose)  
  - Custom MotionEvent handling for advanced multi-touch  
- **Persistence:** JSON (save/load projects)  
- **Export:** Android `Bitmap` → PNG/JPEG → Share sheet  
- **Testing:** JUnit for math/snap logic; Espresso for UI flows  

**Optional Libraries (to evaluate):**
- [Accompanist Gesture](https://google.github.io/accompanist/) — advanced gesture support  
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) — state persistence  
- [Turbine](https://cashapp.github.io/turbine/) — for Flow testing  

---

## 📌 Core Functionalities

### 1. Canvas Interactions
- **Freehand Drawing**  
  - Pen/finger support, smoothed path rendering  
  - Drawn strokes stored in state (for undo/redo)  
- **Pan/Zoom**  
  - One-finger drag → pan  
  - Two-finger pinch → zoom in/out (scales canvas transform)  
- **Undo/Redo**  
  - History stack (≥20 steps)  
  - Undo freehand, tool placements, and snap points  

---

### 2. Tools

#### 📏 Ruler
- Two-finger drag & rotate to reposition  
- One-finger draw → straight line along edge  
- Snapping:  
  - Common angles (0°, 30°, 45°, 60°, 90°)  
  - Endpoints/midpoints of existing lines  
- Visual hints & haptic feedback on snap  

#### 📐 Set Squares
- Two variants:  
  - 45°  
  - 30°–60°  
- Edge-aligned drawing  
- Snap to canvas grid and existing line segments  

#### 🟠 Protractor
- Place over vertex  
- Measure angle between two rays  
- Snap readout to nearest 1°  
- “Hard snap” at common angles (30°, 45°, 60°, 90°, 120°, 135°, 150°, 180°)  

#### 🔵 Compass (Optional, v2)
- Set radius by dragging  
- Draw arcs/circles snapping to intersections or points  

---

### 3. Snapping System
- **Snaps to:**  
  - Grid (configurable spacing, e.g., 5 mm)  
  - Endpoints & midpoints of segments  
  - Circle centers & intersections  
  - Common angles  
- **Snap radius:**  
  - Dynamic (larger at low zoom, smaller at high zoom)  
- **UX:**  
  - Show ghost preview before commit  
  - Highlight snapped target  
  - Haptic on snap  

---

### 4. HUD & Export
- **Precision HUD:**  
  - Angle display (±0.5°)  
  - Length in cm/mm  
- **Export:**  
  - Save drawing as PNG/JPEG  
  - Use Android Share Sheet  

---

## 📚 Developer Docs (Code Snippets)

(… **[snippets section stays as we already wrote it]** …)

---

## 📂 Current File Structure

/app
  /src/main/java/com/example/snappyruler
    /ui
      DrawingScreen.kt         # Composable entrypoint
      ToolToolbar.kt           # UI controls for tool selection
    /view
      DrawingView.kt           # Custom view (Canvas-based fallback)
    /tools
      RulerTool.kt
      SetSquareTool.kt
      ProtractorTool.kt
      CompassTool.kt
    /core
      CanvasState.kt           # Stores shapes and transforms
      ShapeModels.kt           # Line, Circle, Arc models
      Snapper.kt               # Snapping logic
      SpatialHashGrid.kt       # Proximity search
      GestureCoordinator.kt    # Gesture → tool actions
    /persistence
      ProjectSerializer.kt     # Save/load JSON
      Exporter.kt              # Export PNG/JPEG
    /tests
      SnapperTest.kt
      TransformTest.kt
  build.gradle.kts
  README.md

## 📝 Open TODOs / Future Features

We will implement these **incrementally** (ask Cursor: *“Implement TODO-1”*, then move on).

### 🎨 Core Drawing & Canvas
- [ ] **TODO-1:** Implement freehand drawing with smoothing (Bezier curves).  
- [ ] **TODO-2:** Add pan (one-finger) & zoom (two-finger pinch) gestures to the canvas.  
- [ ] **TODO-3:** Add undo/redo stack (≥20 steps).  

### 📏 Tools
- [ ] **TODO-4:** Implement `RulerTool` — drag/rotate with two fingers, draw straight lines along its edge.  
- [ ] **TODO-5:** Add snapping to common angles (0°, 30°, 45°, 60°, 90°).  
- [ ] **TODO-6:** Snap ruler lines to existing endpoints/midpoints.  
- [ ] **TODO-7:** Implement Set Square tools (45°, 30°–60°).  
- [ ] **TODO-8:** Implement Protractor tool with HUD angle readout and snapping to common angles.  
- [ ] **TODO-9:** (Optional) Add Compass tool to draw arcs/circles with snapping.  

### 🎯 Snapping System
- [ ] **TODO-10:** Implement grid snapping with configurable spacing.  
- [ ] **TODO-11:** Add snapping to intersections (line–line, circle–line).  
- [ ] **TODO-12:** Implement hysteresis (sticky snap) and dynamic snap radius based on zoom.  
- [ ] **TODO-13:** Add snap priority rules (endpoint > intersection > midpoint > grid).  

### 📚 UX & HUD
- [ ] **TODO-14:** Implement Precision HUD overlay showing length (cm) and angle (°).  
- [ ] **TODO-15:** Add haptic feedback on snap lock.  
- [ ] **TODO-16:** Add visual indicators (highlight snapped points/angles).  

### 💾 Persistence & Export
- [ ] **TODO-17:** Implement project save/load as JSON.  
- [ ] **TODO-18:** Add PNG/JPEG export with Android Share Sheet.  

### 🛠️ Polish & Performance
- [ ] **TODO-19:** Profile drawing performance with 1000+ shapes, ensure 60 FPS.  
- [ ] **TODO-20:** Add calibration option for DPI vs real-world mm.  
- [ ] **TODO-21:** Add unit tests for snapper, transforms, undo/redo.  
