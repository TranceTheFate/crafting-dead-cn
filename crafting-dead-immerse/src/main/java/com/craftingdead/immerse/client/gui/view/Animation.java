package com.craftingdead.immerse.client.gui.view;

import javax.annotation.Nullable;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;

public class Animation<T> extends TimingTargetAdapter {

  private final ValueStyleProperty<T> property;
  private KeyFrames<T> keyFrames;

  private final boolean fromCurrent;

  private Animation(ValueStyleProperty<T> property, KeyFrames<T> keyFrames, boolean fromCurrent) {
    this.property = property;
    this.keyFrames = keyFrames;
    this.fromCurrent = fromCurrent;
  }

  @Override
  public void timingEvent(Animator source, double fraction) {
    this.property.set(this.keyFrames.getInterpolatedValueAt(fraction));
  }

  @Override
  public void begin(Animator source) {
    if (this.fromCurrent) {
      final T startValue = this.property.get();
      final KeyFrames.Builder<T> builder = new KeyFrames.Builder<>(startValue);
      boolean first = true;
      for (KeyFrames.Frame<T> frame : this.keyFrames) {
        if (first) {
          first = false;
        } else {
          builder.addFrame(frame);
        }
      }
      this.keyFrames = builder.build();
    }

    final double fraction = source.getCurrentDirection() == Direction.FORWARD ? 0.0 : 1.0;
    this.timingEvent(source, fraction);
  }

  public static <T> Builder<T> forProperty(ValueStyleProperty<T> property) {
    return new Builder<>(property);
  }

  public static class Builder<T> {

    private final ValueStyleProperty<T> property;
    private KeyFrames<T> keyFrames;
    private boolean fromCurrent;

    private Builder(ValueStyleProperty<T> property) {
      this.property = property;
    }

    public Builder<T> to(T value) {
      return this.to(null, value);
    }

    public Builder<T> to(@Nullable T from, T value) {
      if (from == null) {
        this.fromCurrent = true;
      }
      this.keyFrames = new KeyFrames.Builder<T>()
          .addFrame(from == null ? value : from)
          .addFrame(value)
          .build();
      return this;
    }

    public Builder<T> keyFrames(KeyFrames<T> keyFrames) {
      this.keyFrames = keyFrames;
      return this;
    }

    public Animation<T> build() {
      return new Animation<>(this.property, this.keyFrames, this.fromCurrent);
    }
  }
}
