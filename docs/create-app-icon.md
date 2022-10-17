#### Create app icon

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

   1. Set the fill colour

      Select everything in the document (Ctrl+A), right-click > _Fill and Stroke_ > _Stroke paint_ > _RGBA_ > `ffffffff` (or whatever colour you want to set it to)

   1. (Optional) Fill in the icon

      1. Make sure the paths for every area you want to fill are enclosed (they should form a loop with no loose ends)

         **Note:** It isn't completely necessary to have each path fully enclosed. Sometimes having it mostly enclosed may be enough

         1. In the left menu, select _Edit paths by nodes_

         1. Shift-click all paths that aren't enclosed that you would like to join

         1. _Path_ > _Combine_

         1. If this creates gaps in the outline, select the joined path > right-click > _Fill and Stroke_ > _Stroke style_ > and choose a different _Cap_ (like the second one) to fill the gaps

         1. If the path is still open because one or more sides don't have paths (e.g. you have a rectangle with 3 sides but the 4th side doesn't have a path, so it's open on that side)

            1. Click the path to select it

            1. Shift-click the actual nodes in the path where a new segment needs to be created

            1. Click _Join selected endnodes with a new segment_

      1. Click to select a path that's completely enclosed

      1. Right-click > _Fill and Stroke_ > _Fill_ > select _Flat color_ (the second box) and set the colour in _RGBA_

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

   1. _File_ > _New_ > _Image Asset_

      - _Foreground Layer_

        1. Browse to media/app-icon.svg
        1. Use an appropriate zoom level, e.g. 40%

      - _Background Layer_

        1. _Color_ > `03DAC5`

   1. _Next_
   1. _Res Directory_ > _main_
   1. _Finish_

#### Create feature graphic (for app stores) from app icon

1. Open app icon in Inkscape

1. Set a background colour

   1. _File_ > _Document Properties_ > _Background_

   1. Uncheck _Checkerboard background_

   1. Click the box to the right of _Background color_

   1. Set a colour and make sure the alpha (`A`) is 255 (or make sure the last 2 digits of `RGBA` is `ff`), e.g. `03dac5ff`

1. Export the image to a PNG

   1. _File_ > _Export PNG Image_

   1. Under _Export area_ click _Page_

   1. Set the values under _Export_ area as follows

      1. _y0_: Set to `-50`

         TODO: maybe it would be better to set these based on a percentage of _y1_?

      1. _y1_: Start with the current value of _y1_, add `50`, e.g. if _y1_ is `453`:

         453 + 50 = 503

      1. _x0_: Start with the value of _Height_, multiply it by `1024` and divide by `500`. Then divide that by `2` and subtract it from the current value of _x1_, e.g. if the value of _Height_ is `553` and the current value of _x1_ is `425`:

         (425 - (553 \* 1024 / 500)) / 2 = -353.772

      1. _x1_: Make the new value of _x0_ positive and add it to the current value of _x1_, e.g. if the new value of _x0_ is `-353.772` and the current value of _x1_ is `425`:

         353.772 + 425 = 778.772

   1. Under _Image size_ set _Width_ to `1024` and make sure _Height_ is `500`. If _Height_ isn't `500`, a mistake was made in the previous section

   1. Click _Export As_ > browse to `metadata/en-US/images/featureGraphic.png`

#### Modify app icon

1. Open app icon ([../media/app-icon.svg](../media/app-icon.svg)) in Inkscape

1. Re-create the app icon (see _Create the app icon_ above)

   ⚠ Make sure the app/src/main/res/mipmap-\* files get replaced and not deleted. If they're deleted, repeat the steps to re-create the app icon. Otherwise, the build will fail with an error such as:

   ```
   > Android resource linking failed
     /home/runner/work/android-pitchpipe/android-pitchpipe/app/build/intermediates/packaged_manifests/debug/AndroidManifest.xml:11: error: resource mipmap/ic_launcher (aka io.bmaupin.pitchpipe:mipmap/ic_launcher) not found.
   ```

1. Re-create the feature graphic (see above)
