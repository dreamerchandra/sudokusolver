# sudokusolver
Point this app towards sudoku and booms it will get solved!!
<h1>About this project</h1>
This is an android app which is build using opencv and Auto ML. THis project uses Opencv to detect the sudoku from the camera and uses AutoML to recognize digits from the image.
<br>
This project is an wrapper on <a href="https://github.com/prajwalkr/SnapSudoku">prajwalkr sudoku solving algorithm using python</a>. The same algorithm is applied on android whereas his project is on python.

<h2>Algorithm</h2>
<ol>
  <li>Preprocessing of image -<b>Grayscale, Thresholding</b></li>
  <li>Getting all the rectangle</li>
  <ol>
    <li><b>Canny edge detection</b> is applied followed by Gaussian Blur to remove noise</li>
    <li>Contour edge detection is applied to get the rectangles in the image</li>
  </ol>
  <li>Highlighting the sudoku</li>
  <ol>
    <li>Sorting the rectangles based on area in descending order of its contour area</li>
    <li>Calculate the distance between the corner for top 5 sorted rectangle in order to find the square</li>
    <li>Highlight that contour. <b>Most probability it will be the sudoku</b></li>
  </ol>
  <li>Once the user feels sudoku is highlighted properly. Scan button can be pressed.</li>
  <li>Wrap perspective transformation is applied on the sudoku</li>
  <li>On clicking on the extract button sudoku is sliced evenly to get the individual cells</li>
  <li>AutoML is applied on the individual cells to get the corresponding numbers in the cells</li>
  <li>Currently backtracking is used to solve sudoku.</li>
</ol> 

<h2>Working</h2>
  <div align="center">
  <a href="http://www.youtube.com/watch?v=EpD2xlgQ-3w"><img src="http://img.youtube.com/vi/EpD2xlgQ-3w/0.jpg" alt="Sudoku   solver"></a>
  Click on the image to play the video
  </div>
