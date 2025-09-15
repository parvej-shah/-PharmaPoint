# 🚀 IntelliJ IDEA Setup Instructions for PharmaPoint

## ✅ Issues Fixed

1. **SQLite JDBC Driver**: Moved JAR files to `/lib` directory and updated module configuration
2. **Classpath Configuration**: Updated `.iml` file to include correct library paths
3. **Run Configuration**: Created proper run configuration with native access flags

## 🔧 IntelliJ IDEA Setup Steps

### Step 1: Refresh Project
1. Open IntelliJ IDEA
2. Go to `File` → `Reload Gradle Project` (if using Gradle) or `File` → `Synchronize`
3. Or simply restart IntelliJ IDEA

### Step 2: Verify Library Configuration
1. Go to `File` → `Project Structure` (Ctrl+Alt+Shift+S)
2. Select `Modules` → `PharmaPoint`
3. Go to `Dependencies` tab
4. Ensure you see:
   - `sqlite-jdbc-3.45.3.0.jar`
   - `slf4j-api-2.0.13.jar`
5. If not visible, click `+` → `JARs or directories` → Add the JAR files from `/lib` folder

### Step 3: Use the Pre-configured Run Configuration
1. In the top toolbar, you should see a run configuration dropdown
2. Select `PharmaPoint Main` from the dropdown
3. Click the green play button to run

### Step 4: Alternative - Create Manual Run Configuration
If the pre-configured one doesn't appear:

1. Go to `Run` → `Edit Configurations`
2. Click `+` → `Application`
3. Set:
   - **Name**: `PharmaPoint Main`
   - **Main class**: `Main`
   - **VM options**: `--enable-native-access=ALL-UNNAMED`
   - **Working directory**: `$PROJECT_DIR$`
   - **Use classpath of module**: `PharmaPoint`
4. Click `OK`

## 🎯 Verify Setup

Run the application and you should see:
```
Database connection established successfully to: jdbc:sqlite:D:\PharmaPoint\src\db\pharma.db
Database initialized successfully.
```

## 🔧 Alternative: Command Line Execution

If IntelliJ issues persist, you can always use:

### Using Batch File:
```bash
run.bat
```

### Manual Command:
```bash
cd D:\PharmaPoint
java --enable-native-access=ALL-UNNAMED -cp "out\production\PharmaPoint;lib\*" Main
```

## ⚠️ Common Issues

1. **If libraries still not found**: Clear IntelliJ cache via `File` → `Invalidate Caches and Restart`
2. **If compilation fails**: Ensure output directory is set to `out\production\PharmaPoint`
3. **If UI doesn't appear**: Check that Swing is properly configured in your JDK

---

> **Note**: The SLF4J warnings are normal and don't affect functionality. They just indicate that no logging framework is configured.
