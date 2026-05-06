# -*- coding: utf-8 -*-
with open('d:/Personales/SISTEMAS/SIFEN/zentra/modulo-api/src/main/java/com/zentra/middleware/api/controller/EmisionController.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Encontrar todas las lineas que contienen definicion de safeInt
matches = [(i+1, line.rstrip()) for i, line in enumerate(lines) if 'safeInt' in line and 'private' in line]
print('Definiciones de safeInt:')
for m in matches:
    print(f'  Linea {m[0]}: {m[1]}')

matches2 = [(i+1, line.rstrip()) for i, line in enumerate(lines) if 'safeDouble' in line and 'private' in line]
print('Definiciones de safeDouble:')
for m in matches2:
    print(f'  Linea {m[0]}: {m[1]}')