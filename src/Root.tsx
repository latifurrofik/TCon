import React from 'react';
import {Composition} from 'remotion';
import {TConShorts} from './Shorts';
import {ThreeEducationalPrototype} from './ThreePrototype';

export const RemotionRoot: React.FC = () => (
  <>
    <Composition
      id="TConShorts"
      component={TConShorts}
      durationInFrames={1500}
      fps={30}
      width={1080}
      height={1920}
      defaultProps={{useGeneratedAssets: false, useNarration: false}}
    />
    <Composition
      id="TConThreePrototype"
      component={ThreeEducationalPrototype}
      durationInFrames={300}
      fps={30}
      width={1080}
      height={1920}
    />
  </>
);
