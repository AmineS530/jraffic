if [ -d "build" ]; then
  echo "Deleting build directory"
  rm -rf build
else
  echo "build directory does not exist"
fi

echo "Running..."
javac -d build  *.java 
cp -r components/assets build/components/assets
java -cp build Main