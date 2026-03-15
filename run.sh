
echo "Running..."
javac -d build  *.java 
cp -r components/assets build/components/assets
java -cp build Main