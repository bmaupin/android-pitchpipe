1. Create an SVG

   1. Install Inkscape

      ```
      sudo apt install inkscape
      ```

   1. Download an image you'd like to convert to an SVG

   1. Enable autosave in Inkscape

      1. _Edit_ > _Preferences_ > _Input/Output_ > _Autosave_ > _Enable autosave_

         **⚠ Don't skip this! Inkscape crashes a lot**

   1. Close and reopen Inkscape

   1. Open the image with Inkscape

   1. (Optional) Try _Path_ > _Trace Bitmap_

      The results for this weren't great for me, so I didn't use it

   1. Trace it manually

   1. Use _Create rectangles and squares_ and trace over any rectangles and squares in the original image

      1. Right-click on the rectangle > _Fill and Stroke_

      1. Under _Fill_ click _No paint_ to remove the fill

      1. Go to _Stroke pain_ > click _Flat color_

      1. Go to _Stroke style_

         1. Set _Width_ as desired

         1. Under _Join_, click the first option to round corners

         1. In the main screen with the rectangle selected, one of the corners should have a circle. Drag it to change the rounded corner radius as desired

   1. From the left menu, use _Draw Bezier curves and straight lines_ to trace over straight lines that aren't part of rectangles

      You can also use _Edit paths by nodes_ from the left menu to adjust the curve after it's created, by clicking in the middle and dragging

      1. Left-click to create each point of the line, right-click when done

      1. Right-click on the line > _Fill and Stroke_

      1. Go to _Stroke style_

      1. Set _Width_ to the same value used for rectangles, for consistency

      1. Under _Cap_, select the second option to round the caps

   1. If there are any similar lines that are reversed, copy existing lines and then use _Object_ > _Flip Horizontal/Vertical_

   1. To draw curved lines, select _Draw Bezier curves and straight lines_ from the left menu

      1. Go to the first point, left-click **and hold**, releasing the mouse button when you're at the halfway point of the curve

      1. Then you should be able to left-click at the second point of the curved line

   1. Shift-click to select multiple items, which you can copy/flip as needed

   1. Once you're done tracing, you can select the original image and delete it

   1. You can select all the paths using _Select and transform objects_ from the left menu and creating a box around everything

   1. From there, you can for example go to _Object_ > _Fill and Stroke_ and adjust properties across all objects, such as the _Width_

   1. Set a dark document background

      _File_ > _Document Properties_ > _Page_ > _Background_ > check _Checkerboard background_

   1. Change the fill colour to white

      Select everything in the document, right-click > _Fill and Stroke_ > _Stroke paint_ > RGBA: `ffffffff`

1. Add a shadow

   You can create a long shadow (recommended) or a drop shadow

   - Create a long shadow

     [https://graphicdesign.stackexchange.com/a/77321/173042](https://graphicdesign.stackexchange.com/a/77321/173042)

     - There are additional instructions here: https://www.klaasnotfound.com/2016/09/12/creating-material-icons-with-long-shadows/
     - Create a black shadow with 10% opactiy

   - Or create a drop shadow

     1. Open the SVG in Inkscape

     1. Select everything in the document (Ctrl+A)

     1. If the objects haven't been grouped yet, group them

        _Object_ > _Group_

     1. Duplicate the object

        _Edit_ > _Duplicate_

     1. Go to _Object_ > _Objects_ and hide the first object (the top one)

     1. Select the other object

     1. _Object_ > _Ungroup_

     1. Right-click > _Fill and Stroke_ > _Stroke paint_ > set _RGBA_ to `000000ff`

     1. _Path_ > _Combine_

        (Without this, the opacity in each path will combine and look darker where each path overlaps)

     1. In _Fill and Stroke_ > _Stroke style_ you may need to set the _Cap_ to _Rounded cap_ to fill in gaps

     1. _Stroke style_ > _Opacity_ > 33.3%

        ⓘ Inkscape also has an option for _Blur_, but don't bother with it as the Android Asset Studio will ignore it 😕

     1. Move the shadow

        1. Choose _Select and transform objects_ from the left menu, select all (Ctrl+A) then unselect the top layer (Shift-click)

        1. While the shadow is still selected, move it down using the arrow key; press the arrow key 5 times (to move it down 10 pixels)

   1. Save the image

1. Create the app icon

   [https://developer.android.com/studio/write/image-asset-studio](https://developer.android.com/studio/write/image-asset-studio)

   Use an appropriate zoom level, e.g. 40%
