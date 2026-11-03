import React from 'react';
import {ThreeCanvas} from '@remotion/three';
import {interpolate, useCurrentFrame, useVideoConfig} from 'remotion';

const NAVY = '#101B2A';
const SLATE = '#263B49';
const CYAN = '#20B8CC';
const CORAL = '#F07859';
const IVORY = '#E9E5DC';

const Node: React.FC<{position: [number, number, number]; color: string; scale: number; frame: number; delay: number}> = ({position, color, scale, frame, delay}) => {
  const local = Math.max(0, frame - delay);
  const rise = interpolate(local, [0, 35], [-1.8, 0], {extrapolateRight: 'clamp'});
  const pulse = 1 + Math.sin((frame - delay) / 12) * 0.06;
  return (
    <mesh position={[position[0], position[1] + rise, position[2]]} scale={scale * pulse} rotation={[local * 0.008, local * 0.012, 0]}>
      <icosahedronGeometry args={[0.62, 1]} />
      <meshStandardMaterial color={color} roughness={0.34} metalness={0.12} />
    </mesh>
  );
};

const SceneObjects: React.FC = () => {
  const frame = useCurrentFrame();
  const {width, height} = useVideoConfig();
  const cameraZ = interpolate(frame, [0, 150, 299], [11, 9.5, 8.8]);
  const rotation = interpolate(frame, [0, 299], [-0.18, 0.22]);
  return (
    <ThreeCanvas width={width} height={height} camera={{fov: 42, position: [0, 0, cameraZ]}} gl={{alpha: true, antialias: true}}>
      <ambientLight intensity={1.1} />
      <directionalLight position={[4, 7, 8]} intensity={2.2} />
      <pointLight position={[-5, 1, 3]} intensity={35} color={CYAN} />
      <group rotation={[0, rotation, 0]}>
        <mesh position={[0, 0, -1.1]} rotation={[0.2, frame * 0.004, 0]}>
          <torusGeometry args={[2.65, 0.035, 12, 100]} />
          <meshStandardMaterial color={CYAN} emissive={CYAN} emissiveIntensity={0.55} />
        </mesh>
        <mesh position={[0, 0, -1.25]} rotation={[Math.PI / 2, 0, frame * -0.003]}>
          <torusGeometry args={[1.95, 0.028, 12, 100]} />
          <meshStandardMaterial color={CORAL} emissive={CORAL} emissiveIntensity={0.35} />
        </mesh>
        <Node position={[-2.15, 0.95, 0]} color={CYAN} scale={0.92} frame={frame} delay={0} />
        <Node position={[0, 1.75, -0.2]} color={IVORY} scale={0.72} frame={frame} delay={18} />
        <Node position={[2.1, 0.65, 0]} color={CORAL} scale={0.95} frame={frame} delay={36} />
        <Node position={[-1.25, -1.35, 0.25]} color={SLATE} scale={1.08} frame={frame} delay={54} />
        <Node position={[1.15, -1.45, 0.1]} color={CYAN} scale={0.82} frame={frame} delay={72} />
        <mesh position={[0, 0, 0.45]} rotation={[frame * 0.006, frame * 0.009, frame * 0.004]}>
          <dodecahedronGeometry args={[0.95, 0]} />
          <meshStandardMaterial color={IVORY} roughness={0.28} metalness={0.18} />
        </mesh>
      </group>
      <mesh position={[0, -3.2, -2]} rotation={[-Math.PI / 2, 0, 0]}>
        <circleGeometry args={[5.8, 64]} />
        <meshBasicMaterial color={NAVY} transparent opacity={0.8} />
      </mesh>
    </ThreeCanvas>
  );
};

export const ThreeEducationalPrototype: React.FC = () => {
  const frame = useCurrentFrame();
  const titleOpacity = interpolate(frame, [0, 22, 260, 299], [0, 1, 1, 0.85], {extrapolateRight: 'clamp'});
  const titleY = interpolate(frame, [0, 24], [35, 0], {extrapolateRight: 'clamp'});
  const progress = (frame / 299) * 100;
  return (
    <div style={{flex: 1, background: 'radial-gradient(ellipse at 50% 43%, #263B49 0%, #101B2A 58%, #080e18 100%)', color: IVORY, fontFamily: 'Arial, sans-serif', overflow: 'hidden', position: 'relative'}}>
      <div style={{position: 'absolute', inset: 0, opacity: 0.12, backgroundImage: 'linear-gradient(#E9E5DC 1px, transparent 1px), linear-gradient(90deg, #E9E5DC 1px, transparent 1px)', backgroundSize: '90px 90px'}} />
      <div style={{position: 'absolute', inset: 0}}><SceneObjects /></div>
      <div style={{position: 'absolute', top: 115, left: 70, right: 70, textAlign: 'center', opacity: titleOpacity, transform: `translateY(${titleY}px)`}}>
        <div style={{fontSize: 25, fontWeight: 800, letterSpacing: 8, color: CYAN}}>TCON • VISUAL LAB</div>
        <div style={{fontSize: 66, lineHeight: 1.08, fontWeight: 900, marginTop: 28, textShadow: '0 8px 30px #0008'}}>BAGAIMANA ALGORITMA<br/>MENGHUBUNGKAN KITA?</div>
      </div>
      <div style={{position: 'absolute', bottom: 150, left: 90, right: 90, textAlign: 'center', fontSize: 31, lineHeight: 1.35, fontWeight: 600, color: IVORY, opacity: titleOpacity}}>
        Sinyal kecil diproses menjadi rekomendasi yang terus bergerak.
      </div>
      <div style={{position: 'absolute', left: 0, right: 0, bottom: 0, height: 12, background: '#ffffff18'}}>
        <div style={{height: '100%', width: `${progress}%`, background: CORAL}} />
      </div>
    </div>
  );
};
