import React from 'react';
import {Composition} from 'remotion';
import {TConShorts} from './Shorts';

export const Root: React.FC = () => (
  <Composition
    id="TConShorts"
    component={TConShorts}
    durationInFrames={1500}
    fps={30}
    width={1080}
    height={1920}
  />
);