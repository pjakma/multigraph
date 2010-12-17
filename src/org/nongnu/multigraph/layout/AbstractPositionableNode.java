package org.nongnu.multigraph.layout;

public abstract class AbstractPositionableNode
                implements PositionableNode {
  Vector2D position = new Vector2D (0,0);
  Vector2D velocity = new Vector2D (0,0);
  float mass = 1;
  float size = 1;
  
  @Override
  public float getMass () {
    return mass;
  }
  
  @Override
  public Vector2D getPosition () {
    return position;
  }
  
  @Override
  public void setPosition (Vector2D p) {
    this.position = p;
  }
  
  @Override
  public Vector2D getVelocity () {
    return velocity;
  }
  
  @Override
  public void setMass (float m) {
    mass = m;
  }
  
  @Override
  public float getSize () {
    return size;
  }
  
  @Override
  public void setSize (float s) {
    this.size = s;
  }
  
  @Override
  public boolean isMovable () {
    return true;
  }

}
