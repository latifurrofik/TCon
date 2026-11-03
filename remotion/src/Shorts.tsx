import React from 'react';
import {AbsoluteFill, interpolate, useCurrentFrame, useVideoConfig} from 'remotion';
import {episode01} from './episode01';

const gold = '#f5cf58';
const scenes = episode01.scenes;

export const TConShorts: React.FC = () => {
  const frame = useCurrentFrame();
  const {fps} = useVideoConfig();
  const scene = [...scenes].reverse().find((s) => frame >= s.start) ?? scenes[0];
  const local = frame - scene.start;
  const progress = interpolate(local, [0, scene.duration], [0, 1], {extrapolateRight: 'clamp'});
  const zoom = interpolate(progress, [0, 1], [1, 1.08]);
  const fade = interpolate(local, [0, 12, scene.duration - 12, scene.duration], [0, 1, 1, 0], {extrapolateRight: 'clamp'});

  return (
    <AbsoluteFill style={{backgroundColor: '#080d19', color: '#fff', fontFamily: 'Arial, sans-serif', overflow: 'hidden'}}>
      <AbsoluteFill style={{background: 'radial-gradient(ellipse at 50% 42%, #26334a 0%, #101827 48%, #050811 100%)'}} />
      <AbsoluteFill style={{opacity: 0.18, transform: `scale(${zoom})`, backgroundImage: 'linear-gradient(135deg, transparent 48%, #71809a 49%, transparent 50%)', backgroundSize: '90px 90px'}} />
      <AbsoluteFill style={{padding: '100px 76px', justifyContent: 'space-between', opacity: fade}}>
        <div style={{fontSize: 30, fontWeight: 700, letterSpacing: 4, color: gold}}>TCON • PSIKOLOGI DIGITAL</div>
        <div style={{alignItems: 'center', display: 'flex', flex: 1, justifyContent: 'center', flexDirection: 'column', textAlign: 'center'}}>
          <div style={{width: 460, height: 460, borderRadius: '50%', border: '3px solid #64748b', background: 'radial-gradient(circle, #52627a 0%, #172236 58%, #0b1220 100%)', boxShadow: '0 0 100px #31425b66', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 180}}>◉</div>
          <div style={{marginTop: 90, fontSize: 66, fontWeight: 900, lineHeight: 1.08, textShadow: '0 5px 18px #000'}}>{scene.label}</div>
          <div style={{marginTop: 34, width: 780, height: 7, background: '#ffffff30', borderRadius: 9}}>
            <div style={{width: `${progress * 100}%`, height: '100%', background: gold, borderRadius: 9}} />
          </div>
        </div>
        <div style={{fontSize: 34, color: '#e7eaf0', lineHeight: 1.35, textAlign: 'center'}}>EPISODE 01  •  {episode01.title}</div>
      </AbsoluteFill>
      <div style={{position: 'absolute', bottom: 38, left: 76, right: 76, height: 5, background: '#ffffff22'}}>
        <div style={{height: '100%', width: `${(frame / 1500) * 100}%`, background: gold}} />
      </div>
    </AbsoluteFill>
  );
};