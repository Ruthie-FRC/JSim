Write-Host "=== JSIM RESTRUCTURE START ==="

# 1. delete build artifacts safely
Remove-Item -Recurse -Force build, out, bin -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force .gradle -ErrorAction SilentlyContinue

# 2. create directories
$dirs = @(
  "core/cpp","core/jni",
  "jsim/api","jsim/sim","jsim/field",
  "apps/runtime","apps/viewer",
  "field-pipeline/cad-import","field-pipeline/definitions","field-pipeline/exporters",
  "docs/content","docs/assets"
)

foreach ($d in $dirs) {
  New-Item -ItemType Directory -Force -Path $d | Out-Null
}

# 3. move C++ core
if (Test-Path "core/driver/include") {
  Move-Item "core/driver/include/*" "core/cpp/" -ErrorAction SilentlyContinue
}
if (Test-Path "core/driver/src") {
  Move-Item "core/driver/src/*" "core/cpp/" -ErrorAction SilentlyContinue
}

# 4. move JNI if present
Move-Item "core/cpp/JSimJNI.cpp" "core/jni/" -ErrorAction SilentlyContinue
Move-Item "core/cpp/math_jni.cpp" "core/jni/" -ErrorAction SilentlyContinue

# remove old driver
Remove-Item -Recurse -Force "core/driver" -ErrorAction SilentlyContinue

# 5. move Java API
if (Test-Path "jsim/java") {
  Move-Item "jsim/java/api/*" "jsim/api/" -ErrorAction SilentlyContinue
  Move-Item "jsim/java/frc/*" "jsim/sim/" -ErrorAction SilentlyContinue
  Move-Item "jsim/java/nt/*" "jsim/sim/" -ErrorAction SilentlyContinue
  Move-Item "jsim/java/field/*" "jsim/field/" -ErrorAction SilentlyContinue
  Remove-Item -Recurse -Force "jsim/java" -ErrorAction SilentlyContinue
}

# 6. apps
if (Test-Path "apps/sim-runtime") {
  Move-Item "apps/sim-runtime/*" "apps/runtime/" -ErrorAction SilentlyContinue
}
if (Test-Path "apps/viewer-plugin") {
  Move-Item "apps/viewer-plugin/*" "apps/viewer/" -ErrorAction SilentlyContinue
}
Remove-Item -Recurse -Force "apps/sim-runtime","apps/viewer-plugin" -ErrorAction SilentlyContinue

# 7. field pipeline
if (Test-Path "cad-import") {
  Move-Item "cad-import/*" "field-pipeline/cad-import/" -ErrorAction SilentlyContinue
  Remove-Item -Recurse -Force "cad-import" -ErrorAction SilentlyContinue
}

Move-Item "field_*.py" "field-pipeline/definitions/" -ErrorAction SilentlyContinue
Move-Item "FIELD_RECREATION_GUIDE.md" "field-pipeline/" -ErrorAction SilentlyContinue

# 8. docs flatten
if (Test-Path "mkdocs/docs") {
  Move-Item "mkdocs/docs/*" "docs/content/" -ErrorAction SilentlyContinue
}
if (Test-Path "mkdocs/docs/assets") {
  Move-Item "mkdocs/docs/assets/*" "docs/assets/" -ErrorAction SilentlyContinue
}
Move-Item "mkdocs/mkdocs.yml" "docs/" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "mkdocs" -ErrorAction SilentlyContinue

Write-Host "=== DONE ==="
Write-Host "Run: tree /F"