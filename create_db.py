import sqlite3
import sys
import subprocess

output = subprocess.call("sqlite3 meteorite.db < meteorite.sql", Shell=True)

connection = None

try:
    connection = sqlite3.connect("meteorite.db")
    cursor = connection.cursor()

    cursor.execute()
