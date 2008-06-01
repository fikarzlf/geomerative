/**
    Copyright 2004-2008 Ricard Marxer  <email@ricardmarxer.com>

    This file is part of Geomerative.

    Geomerative is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Geomerative is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with Geomerative.  If not, see <http://www.gnu.org/licenses/>.
*/

package geomerative ;
import processing.core.*;


/**
 * RGroup is a holder for a group of geometric element that can be drawn and transformed, such as Shapes, Polygons or Meshes.
 * @usage geometry
 */
public class RGroup extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.GROUP;
    
  /**
   * Array of RGeomElem objects holding the elements of the group. When accessing theses elements we must cast them to their class in order to get all the functionalities of each representation. e.g. RShape s = group.elements[i].toShape()  If the element cannot be converted to the target class it will throw a RuntimeException, to ignore these, use try-catch syntax.
   * @eexample RGroup_elements
   * @related RShape
   * @related RPolygon
   * @related RMesh
   * @related countElements ( )
   * @related addElement ( )
   * @related removeElement ( )
   */
  public RGeomElem[] elements;
  
  /**
   * Use this method to create a new empty group.
   * @eexample RGroup
   */
  public RGroup(){
    elements = null;
  }

  /**
   * Use this method to create a copy of a group.
   * @eexample RGroup
   */
  public RGroup(RGroup grp){
    for(int i=0;i<grp.countElements();i++){
      //System.out.println(grp.elements[i].getType());
      switch(grp.elements[i].getType()){
      case RGeomElem.MESH:
        this.addElement(new RMesh((RMesh)grp.elements[i]));
        break;
        
      case RGeomElem.GROUP:
        this.addElement(new RGroup((RGroup)grp.elements[i]));
        break;
        
      case RGeomElem.POLYGON:
        this.addElement(new RPolygon((RPolygon)grp.elements[i]));
        break;
        
      case RGeomElem.SHAPE:
        this.addElement(new RShape((RShape)grp.elements[i]));
        break;
        
      }
    }
    
    setStyle(grp);
  }
  
  
  /**
   * Use this method to get the centroid of the element.
   * @eexample RGroup_getCentroid
   * @return RPoint, the centroid point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   */
  public RPoint getCentroid(){
    RPoint bestCentroid = new RPoint();
    float bestArea = Float.NEGATIVE_INFINITY;
    if(elements != null){
      for(int i=0;i<elements.length-1;i++)
        {
          float area = elements[i].getArea();
          if(area > bestArea){
            bestArea = area;
            bestCentroid = elements[i].getCentroid();
          }
        }
      return bestCentroid;
    }
    return null;
  }
  
  /**
   * Use this method to count the number of elements in the group.
   * @eexample RGroup_countElements
   * @return int, the number elements in the group.
   * @related addElement ( )
   * @related removeElement ( )
   */
  public int countElements(){
    if(elements==null) return 0;
    return elements.length;
  }
  
  public void print(){
    System.out.println("group: ");
    for(int i=0;i<countElements();i++)
      {
        System.out.println("---  "+i+" ---");
        elements[i].print();
        System.out.println("---------------");
      }
  }
    
  /**
   * Use this method to draw the group.  This will draw each element at a time, without worrying about intersections or holes.  This is the main difference between having a shape with multiple subshapes and having a group with multiple shapes.
   * @eexample RGroup_draw
   * @param g PGraphics, the graphics object on which to draw the group
   */
  public void draw(PGraphics g){
    if(!RGeomerative.ignoreStyles){
      saveContext(g);
      setContext(g);
    }

    for(int i=0; i<countElements(); i++){
      elements[i].draw(g);
    }

    if(!RGeomerative.ignoreStyles){
      restoreContext(g);
    }
  }
  
  public void draw(PApplet a){
    if(!RGeomerative.ignoreStyles){
      saveContext(a);
      setContext(a);
    }

    for(int i=0; i<countElements(); i++){
      elements[i].draw(a);
    }
    
    if(!RGeomerative.ignoreStyles){
      restoreContext(a);
    }
  }
  
  /**
   * Use this method to add a new element.
   * @eexample RGroup_addElement
   * @param elem RGeomElem, any kind of RGeomElem to add.  It accepts the classes RShape, RPolygon and RMesh.
   * @related removeElement ( )
   */
  public void addElement(RGeomElem elem){
    this.append(elem);
  }
  
  /**
   * Use this method to add a new element.
   * @eexample RGroup_addGroup
   * @param grupo RGroup, A group of elements to add to this group.
   * @related removeElement ( )
   */
  public void addGroup(RGroup grupo){
    for(int i=0;i<grupo.countElements();i++){
      this.addElement(grupo.elements[i]);
    }
  }
  
  /**
   * Use this method to remove an element.
   * @eexample RGroup_removeElement
   * @param i int, the index of the element to remove from the group.
   * @related addElement ( )
   */
  public void removeElement(int i) throws RuntimeException{
    this.extract(i);
  }
  
  /**
   * Use this method to get a new group whose elements are the corresponding Meshes of the elemnts in the current group.  This can be used for increasing performance in exchange of losing abstraction.
   * @eexample RGroup_toMeshGroup
   * @return RGroup, the new group made of RMeshes
   * @related toPolygonGroup ( )
   * @related toShapeGroup ( )
   */
  public RGroup toMeshGroup() throws RuntimeException{
    RGroup result = new RGroup();
    for(int i=0;i<countElements();i++){
      result.addElement(elements[i].toMesh());
    }
    return result;
  }
  
  /**
   * Use this method to get a new group whose elements are the corresponding Polygons of the elemnts in the current group.  At this moment there is no implementation for transforming aMesh to a Polygon so applying this method to groups holding Mesh elements will generate an exception.
   * @eexample RGroup_toPolygonGroup
   * @return RGroup, the new group made of RPolygons
   * @related toMeshGroup ( )
   * @related toShapeGroup ( )
   */
  public RGroup toPolygonGroup() throws RuntimeException{
    RGroup result = new RGroup();
    for(int i=0;i<countElements();i++){
      RGeomElem element = elements[i];
      if(element.getType() == RGeomElem.GROUP){
        RGeomElem newElement = ((RGroup)(element)).toPolygonGroup();
        newElement.setStyle(element);
        result.addElement(newElement);
      }else{
        result.addElement(element.toPolygon());
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * Use this method to get a new group whose elements are all the corresponding Shapes of the elemnts in the current group.  At this moment there is no implementation for transforming a Mesh or a Polygon to a Shape so applying this method to groups holding Mesh or Polygon elements will generate an exception.
   * @eexample RGroup_toShapeGroup
   * @return RGroup, the new group made of RShapes
   * @related toMeshGroup ( )
   * @related toPolygonGroup ( )
   */
  public RGroup toShapeGroup() throws RuntimeException{
    RGroup result = new RGroup();
    for(int i=0;i<countElements();i++){
      result.addElement(elements[i].toShape());
    }
    return result;
  }
  
  /**
   * @invisible
   */
  public RMesh toMesh() throws RuntimeException{
    //throw new RuntimeException("Transforming a Group to a Mesh is not yet implemented.");
    RGroup meshGroup = toMeshGroup();
    RMesh result = new RMesh();
    for(int i=0;i<countElements();i++){
      RMesh currentMesh = (RMesh)(meshGroup.elements[i]);
      for(int j=0;j<currentMesh.countStrips();j++){
        result.addStrip(currentMesh.strips[j]);
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * @invisible
   */
  public RPolygon toPolygon() throws RuntimeException{
    //throw new RuntimeException("Transforming a Group to a Polygon is not yet implemented.");
    //RGroup polygonGroup = toPolygonGroup();
    RPolygon result = new RPolygon();
    for(int i=0;i<countElements();i++){
      RPolygon currentPolygon = elements[i].toPolygon();
      for(int j=0;j<currentPolygon.countContours();j++){
        result.addContour(currentPolygon.contours[j]);
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * @invisible
   */
  public RShape toShape() throws RuntimeException{
    //throw new RuntimeException("Transforming a Group to a Shape is not yet implemented.");
    RShape result = new RShape();
    for(int i=0;i<countElements();i++){
      RShape currentShape = elements[i].toShape();
      for(int j=0;j<currentShape.countSubshapes();j++){
        result.addSubshape(currentShape.subshapes[j]);
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * Use this to return the points of the group.  It returns the points in the way of an array of RPoint.
   * @eexample RGroup_getHandles
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getHandles(){
    int numElements = countElements();
    if(numElements == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numElements;i++){
      RPoint[] newPoints = elements[i].getHandles();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }
    return result;
  }  
  
  public RPoint getPoint(float t){
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    return elements[indOfElement].getPoint(advOfElement);
  }

  /**
   * Use this to return the points of the group.  It returns the points in the way of an array of RPoint.
   * @eexample RGroup_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getPoints(){
    int numElements = countElements();
    if(numElements == 0){
      return null;
    }

    RCommand.segmentAccOffset = RCommand.segmentOffset;    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numElements;i++){
      RPoint[] newPoints = elements[i].getPoints();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }
    
    return result;
  }


  public RPoint getTangent(float t){
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    return elements[indOfElement].getTangent(advOfElement);
  }

  /**
   * Use this to return the points of the group.  It returns the points in the way of an array of RPoint.
   * @eexample RGroup_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getTangents(){
    int numElements = countElements();
    if(numElements == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numElements;i++){
      RPoint[] newPoints = elements[i].getTangents();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }
    return result;
  }

  
  /**
   * Use this method to get the type of element this is.
   * @eexample RPolygon_getType
   * @return int, will allways return RGeomElem.POLYGON
   */
  public int getType(){
    return type;
  }

  private float[] indAndAdvAt(float t){
    int indOfElement = 0;
    float[] lengthsCurves = getCurveLengths();
    float lengthCurve = getCurveLength();

    /* Calculate the amount of advancement t mapped to each command */
    /* We use a simple algorithm where we give to each command the same amount of advancement */
    /* A more useful way would be to give to each command an advancement proportional to the length of the command */
    /* Old method with uniform advancement per command
       float advPerCommand;
       advPerCommand = 1F / numSubshapes;
       indCommand = (int)(Math.floor(t / advPerCommand)) % numSubshapes;
       advOfCommand = (t*numSubshapes - indCommand);
    */
    
    float accumulatedAdvancement = lengthsCurves[indOfElement] / lengthCurve;
    float prevAccumulatedAdvancement = 0F;
    
    /* Find in what command the advancement point is  */
    while(t > accumulatedAdvancement){
      indOfElement++;
      prevAccumulatedAdvancement = accumulatedAdvancement;
      accumulatedAdvancement += (lengthsCurves[indOfElement] / lengthCurve);
    }
    
    float advOfElement = (t-prevAccumulatedAdvancement) / (lengthsCurves[indOfElement] / lengthCurve);

    float[] indAndAdv = new float[2];

    indAndAdv[0] = indOfElement;
    indAndAdv[1] = advOfElement;
    
    return indAndAdv;
  }

  public RGroup[] split(float t){
    RGroup[] result = new RGroup[2];

    if(t == 0.0F){ 
      result[0] = new RGroup();
      result[1] = new RGroup(this);
      result[0].setStyle(this);
      result[1].setStyle(this);

      return result;
    }
    
    if(t == 1.0F){
      result[0] = new RGroup(this);
      result[1] = new RGroup();
      result[0].setStyle(this);
      result[1].setStyle(this);

      return result;
    }

    result[0] = new RGroup();
    result[1] = new RGroup();

    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    // Add the elements before the cut point
    for(int i=0; i<indOfElement; i++){
      switch(elements[i].getType()){
      case RGeomElem.MESH:
        result[0].addElement(new RMesh((RMesh)elements[i]));
        break;
        
      case RGeomElem.GROUP:
        result[0].addElement(new RGroup((RGroup)elements[i]));
        break;
        
      case RGeomElem.POLYGON:
        result[0].addElement(new RPolygon((RPolygon)elements[i]));
        break;
        
      case RGeomElem.SHAPE:
        result[0].addElement(new RShape((RShape)elements[i]));
        break;
      }
    }

    // Add the cut point element cutted
    RGeomElem element = this.elements[indOfElement];
    switch(element.getType())
      {
      case RGeomElem.GROUP:
        RGroup[] splittedGroups = ((RGroup)element).split(advOfElement);
        if( splittedGroups != null ){
          result[0].addElement(new RGroup(splittedGroups[0]));
          result[1].addElement(new RGroup(splittedGroups[1]));
        }
        break;
        
      case RGeomElem.SHAPE:
        RShape[] splittedShapes = ((RShape)element).split(advOfElement);
        if( splittedShapes != null ){
          result[0].addElement(new RShape(splittedShapes[0]));
          result[1].addElement(new RShape(splittedShapes[1]));
        }
        break;
      }

    // Add the elements after the cut point    
    for(int i=indOfElement+1; i<countElements(); i++){
      switch(elements[i].getType()){
      case RGeomElem.MESH:
        result[1].addElement(new RMesh((RMesh)elements[i]));
        break;
        
      case RGeomElem.GROUP:
        result[1].addElement(new RGroup((RGroup)elements[i]));
        break;
        
      case RGeomElem.POLYGON:
        result[1].addElement(new RPolygon((RPolygon)elements[i]));
        break;
        
      case RGeomElem.SHAPE:
        result[1].addElement(new RShape((RShape)elements[i]));
        break;
      }
    }
    
    result[0].setStyle(this);
    result[1].setStyle(this);
    
    return result;
  }

  public RGroup[] splitAll(float t){
    RGroup[] result = new RGroup[2];
    result[0] = new RGroup();
    result[1] = new RGroup();
    for(int i = 0; i<this.countElements(); i++){
      RGeomElem element = this.elements[i];
      
      switch(element.getType())
        {
        case RGeomElem.GROUP:
          RGroup[] splittedGroups = ((RGroup)element).splitAll(t);
          if( splittedGroups != null ){
            result[0].addElement(splittedGroups[0]);
            result[1].addElement(splittedGroups[1]);
          }
          break;
          
        case RGeomElem.SHAPE:
          RShape[] splittedShapes = ((RShape)element).splitAll(t);
          if( splittedShapes != null ){
            result[0].addElement(splittedShapes[0]);
            result[1].addElement(splittedShapes[1]);
          }
          break;
        }
    }
    result[0].setStyle(this);
    result[1].setStyle(this);

    return result;
  }

  protected void calculateCurveLengths(){
    lenCurves = new float[countElements()];
    lenCurve = 0F;
    for(int i=0;i<countElements();i++){
      lenCurves[i] = elements[i].getCurveLength();  
      lenCurve += lenCurves[i];
    }
  }
  
  
  /**
   * Use this method to adapt a group of of figures to a shape.
   * @eexample RGroup_adapt
   * @param RSubshape sshp, the subshape to which to adapt
   * @return RGroup, the adapted group
   */
  public void adapt(RShape shp, float wght, float lngthOffset) throws RuntimeException{
    RContour c = this.getBounds();
    float xmin = c.points[0].x;
    float xmax = c.points[2].x;
    float ymin = c.points[0].y;
    float ymax = c.points[2].y;
    
    int numElements = this.countElements();
    
    switch(RGeomerative.adaptorType){
    case RGeomerative.BYPOINT:
      for(int i=0;i<numElements;i++){
        RGeomElem elem = this.elements[i];
        RPoint[] ps = elem.getHandles();
        if(ps != null){
          for(int k=0;k<ps.length;k++){
            float px = ps[k].x;
            float py = ps[k].y;
            
            float t = ((px-xmin)/(xmax-xmin) + lngthOffset ) % 1F;
            float amp = (ymax-py);
            
            RPoint tg = shp.getTangent(t);
            RPoint p = shp.getPoint(t);
            float angle = (float)Math.atan2(tg.y, tg.x) - (float)Math.PI/2F;
            
            ps[k].x = p.x + wght*amp*(float)Math.cos(angle);
            ps[k].y = p.y + wght*amp*(float)Math.sin(angle);
          }
        }
      }
      break;
    case RGeomerative.BYELEMENTPOSITION:
      
      for(int i=0;i<numElements;i++){
        RGeomElem elem = this.elements[i];
        RContour elemc = elem.getBounds();
        
        float px = (elemc.points[2].x + elemc.points[0].x) / 2;
        float py = elemc.points[2].y;
        float t = ((px-xmin)/(xmax-xmin) + lngthOffset ) % 1F;
        
        RPoint tg = shp.getTangent(t);
        RPoint p = shp.getPoint(t);
        float angle = (float)Math.atan2(tg.y, tg.x);
        
        RPoint pletter = new RPoint(px,0);
        p.sub(pletter);
        
        RMatrix mtx = new RMatrix();
        mtx.translate(p);
        mtx.rotate(angle,pletter);
        mtx.scale(wght,pletter);
        
        elem.transform(mtx);
      }
      break;
      
    case RGeomerative.BYELEMENTINDEX:
      
      for(int i=0;i<numElements;i++){
        RGeomElem elem = this.elements[i];
        RContour elemc = elem.getBounds();
        
        float px = (elemc.points[2].x + elemc.points[0].x) / 2;
        float py = elemc.points[2].y;
        float t = ((float)i/(float)numElements + lngthOffset ) % 1F;
        
        RPoint tg = shp.getTangent(t);
        RPoint p = shp.getPoint(t);
        float angle = (float)Math.atan2(tg.y, tg.x);
        
        RPoint pletter = new RPoint(px,0);
        p.sub(pletter);
        
        RMatrix mtx = new RMatrix();
        mtx.translate(p);
        mtx.rotate(angle,pletter);
        mtx.scale(wght,pletter);
        
        elem.transform(mtx);
      }
      break;
      
    default:
      throw new RuntimeException("Unknown adaptor type : "+RGeomerative.adaptorType+". The method RGeomerative.setAdaptor() only accepts RGeomerative.BYPOINT or RGeomerative.BYELEMENT as parameter values.");
    }
  }
  
  public void adapt(RShape shp) throws RuntimeException{
    adapt(shp, RGeomerative.adaptorScale, RGeomerative.adaptorLengthOffset);
  }
  
  public RGroup adaptAll(RShape shp) throws RuntimeException{
    RGroup result = new RGroup();
    int numSubshapes = shp.countSubshapes();
    for(int i=0;i<numSubshapes;i++){
      RGroup tempresult = new RGroup(this);
      tempresult.adapt(shp.subshapes[i].toShape());
      int numElements = tempresult.countElements();
      for(int j=0;j<numElements;j++){
        result.addElement(tempresult.elements[j]);
      }
    }
    return result;
  }
  
  private void append(RGeomElem elem){
    RGeomElem[] newelements;
    if(elements==null){
      newelements = new RGeomElem[1];
      newelements[0] = elem;
    }else{
      newelements = new RGeomElem[this.elements.length+1];
      System.arraycopy(this.elements,0,newelements,0,this.elements.length);
      newelements[this.elements.length]=elem;
    }
    this.elements=newelements;
  }
  
  private void extract(int i) throws RuntimeException{
    RGeomElem[] newelements;
    if(elements==null){
      throw new RuntimeException("The group is empty. No elements to remove.");
    }else{
      if(i<0){
        throw new RuntimeException("Negative values for indexes are not valid.");
      }
      if(i>elements.length-1){
        throw new RuntimeException("Index out of the bounds of the group.  You are trying to erase an element with an index higher than the number of elements in the group.");
      }
      if(elements.length==1){
        newelements = null;
      }else if(i==0){
        newelements = new RGeomElem[this.elements.length-1];
        System.arraycopy(this.elements,1,newelements,0,this.elements.length-1);
      }else if(i==elements.length-1){
        newelements = new RGeomElem[this.elements.length-1];
        System.arraycopy(this.elements,0,newelements,0,this.elements.length-1);
      }else{
        newelements = new RGeomElem[this.elements.length-1];
        System.arraycopy(this.elements,0,newelements,0,i);
        System.arraycopy(this.elements,i+1,newelements,i,this.elements.length-i-1);
      }
    }
    this.elements=newelements;
  }
}
