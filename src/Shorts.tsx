import React from 'react';
import {AbsoluteFill, Audio, Img, interpolate, staticFile, useCurrentFrame} from 'remotion';
import {episode01, Scene} from './episode';

const BG = '#080d18';
const YELLOW = '#ffd447';

type Props = {useGeneratedAssets?: boolean; useNarration?: boolean};

const Symbol: React.FC<{type: Scene['visual']; progress: number}> = ({type, progress}) => {
  const scale = 0.88 + progress * 0.12;
  const common: React.CSSProperties = {transform: `scale(${scale})`, transition: 'none'};
  if (type === 'phone') return <div style={{...common,width:250,height:430,border:'12px solid #e8edf5',borderRadius:42,background:'#151f31',display:'flex',alignItems:'center',justifyContent:'center',boxShadow:'0 0 90px #ffd44733'}}><div style={{width:150,height:150,borderRadius:75,background:YELLOW,display:'flex',alignItems:'center',justifyContent:'center',fontSize:76,color:BG}}>◉</div></div>;
  if (type === 'person') return <div style={{...common,position:'relative',width:300,height:380}}><div style={{position:'absolute',left:95,top:10,width:110,height:110,borderRadius:70,background:'#d7a77b'}}/><div style={{position:'absolute',left:45,top:125,width:210,height:250,borderRadius:'100px 100px 35px 35px',background:'#263a55'}}/><div style={{position:'absolute',left:135,top:170,width:30,height:150,background:YELLOW,transform:'rotate(-22deg)'}}/></div>;
  if (type === 'feed') return <div style={{...common,display:'flex',gap:18,transform:`translateY(${progress*-35}px) scale(${scale})`}}>{[0,1,2].map((n)=><div key={n} style={{width:170,height:300,borderRadius:24,background:['#263a55','#6c453d','#344c43'][n],border:'3px solid #ffffff22',display:'flex',alignItems:'center',justifyContent:'center',fontSize:64,color:YELLOW}}>{['▶','♥','↻'][n]}</div>)}</div>;
  return <div style={{...common,width:330,height:330,borderRadius:180,border:`12px solid ${YELLOW}`,display:'flex',alignItems:'center',justifyContent:'center',fontSize:220,color:YELLOW,fontWeight:900}}>?</div>;
};

export const TConShorts: React.FC<Props> = ({useGeneratedAssets = false, useNarration = false}) => {
  const frame = useCurrentFrame();
  const scene = episode01.scenes.find((s) => frame >= s.start && frame < s.end) ?? episode01.scenes[0];
  const local = frame - scene.start;
  const duration = scene.end - scene.start;
  const progress = interpolate(local,[0,duration],[0,1],{extrapolateLeft:'clamp',extrapolateRight:'clamp'});
  const enter = interpolate(local,[0,24],[0,1],{extrapolateRight:'clamp'});
  const exit = interpolate(local,[duration-18,duration],[1,0],{extrapolateLeft:'clamp'});
  const opacity = Math.min(enter,exit);
  const drift = interpolate(local,[0,duration],[35,-35]);
  return <AbsoluteFill style={{background:BG,color:'#f5f7fb',fontFamily:'Arial, Helvetica, sans-serif',padding:'100px 76px',justifyContent:'space-between',overflow:'hidden'}}>
    {useNarration && <Audio src={staticFile(scene.audio)} volume={1}/>}
    <AbsoluteFill style={{background:'radial-gradient(ellipse at 50% 43%, #1b2b42 0%, #080d18 66%)'}}/>
    <div style={{position:'absolute',top:0,left:0,width:'100%',height:14,background:'#253247'}}><div style={{height:'100%',width:`${frame/1500*100}%`,background:YELLOW}}/></div>
    <div style={{zIndex:1,opacity,fontSize:27,fontWeight:800,letterSpacing:5,color:YELLOW}}>{scene.eyebrow}</div>
    <div style={{zIndex:1,display:'flex',flexDirection:'column',alignItems:'center',gap:70,transform:`translateY(${drift}px)`,opacity}}>
      <div style={{fontSize:76,lineHeight:1.12,fontWeight:900,textAlign:'center',whiteSpace:'pre-line',letterSpacing:-2,textShadow:'0 8px 30px #0008'}}>{scene.headline}</div>
      {useGeneratedAssets ? <Img src={staticFile(scene.image)} style={{width:760,height:760,objectFit:'contain'}}/> : <Symbol type={scene.visual} progress={progress}/>}
      <div style={{fontSize:38,lineHeight:1.35,textAlign:'center',maxWidth:850,color:'#d6deeb'}}>{scene.caption}</div>
    </div>
    <div style={{zIndex:1,display:'flex',justifyContent:'space-between',alignItems:'center',fontSize:24,fontWeight:800,letterSpacing:4,color:'#8794a8'}}>
      <span>TCON</span><span style={{color:YELLOW}}>ADA APA SEBENARNYA?</span>
    </div>
  </AbsoluteFill>;
};
