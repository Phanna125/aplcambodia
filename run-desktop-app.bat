@echo off
title Coffee Ordering System - Desktop App
cd /d "%~dp0"
echo Starting Coffee Ordering System Desktop App...
java -cp desktop-app/bin com.coffeeshop.swing.CoffeeShopApp
pause
