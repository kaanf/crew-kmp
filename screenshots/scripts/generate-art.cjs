// Original, fictional illustration assets. No remote images or real user likenesses.
// Run with NODE_PATH pointing at Goldie 0.5.0/node_modules.
const { createCanvas, GlobalFonts } = require('@napi-rs/canvas');
const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname, '../..');
const out = path.join(root, 'screenshots/shared/src/commonMain/composeResources/files');
GlobalFonts.registerFromPath(path.join(root, 'core/designsystem/src/commonMain/composeResources/font/BricolageGrotesque-Bold.ttf'), 'Crew');
const colors = ['#C8FF3D','#9D7AFF','#FF9EBB','#5BE0C5','#FFB341','#6FB7FF','#FF7A5C','#E4D3FF'];
function circle(ctx,x,y,r,color) { ctx.fillStyle=color; ctx.beginPath();ctx.arc(x,y,r,0,Math.PI*2);ctx.fill(); }
function person(ctx,x,y,scale,i) {
 ctx.save();ctx.translate(x,y);ctx.scale(scale,scale);
 const color=colors[i%8];
 ctx.fillStyle=color;ctx.beginPath();ctx.ellipse(0,190,115,150,0,0,Math.PI*2);ctx.fill();
 circle(ctx,-65,0,18,'#F6EFE3');circle(ctx,65,0,18,'#F6EFE3');
 circle(ctx,0,0,68,'#F6EFE3');circle(ctx,0,-4,56,'#181410');
 for(let j=0;j<7;j++) { const a=Math.PI+j*Math.PI/6; circle(ctx,Math.cos(a)*53,Math.sin(a)*53-12,23,color); }
 circle(ctx,-19,-1,7,'#F6EFE3');circle(ctx,20,-1,7,'#F6EFE3');
 ctx.strokeStyle='#F6EFE3';ctx.lineWidth=7;ctx.lineCap='round';ctx.beginPath();ctx.arc(0,12,23,.18,Math.PI-.18);ctx.stroke();ctx.restore();
}
function save(name,w,h,draw) {const c=createCanvas(w,h);const ctx=c.getContext('2d');draw(ctx);fs.writeFileSync(path.join(out,name),c.toBuffer('image/png'));}
for(let i=0;i<8;i++)save(`avatar-${i+1}.png`,256,256,c=>{c.fillStyle='#2C241D';c.fillRect(0,0,256,256);person(c,128,118,1.25,i);});
save('event.png',1000,1200,c=>{
 c.fillStyle='#9D7AFF';c.fillRect(0,0,1000,1200);
 c.strokeStyle='#181410';c.lineWidth=3;
 for(let x=-500;x<1500;x+=100){c.beginPath();c.moveTo(x,0);c.lineTo(x+700,1200);c.stroke();}
 c.fillStyle='#C8FF3D';c.fillRect(55,60,890,560);
 c.fillStyle='#0E0B08';c.font='bold 42px Crew';c.fillText('CREW PRESENTS',100,135);
 c.font='bold 140px Crew';c.fillText('THE',90,285);c.fillText('SOCIAL',90,420);c.fillText('CLUB',90,555);
 person(c,200,820,1.8,0);person(c,500,800,1.9,3);person(c,800,850,1.8,2);
 c.fillStyle='#0E0B08';c.fillRect(0,1090,1000,110);c.fillStyle='#F6EFE3';c.font='bold 36px Crew';c.fillText('24 OCT   /   THE LANTERN   /   19:30',55,1160);
});
for(let i=1;i<=4;i++)save(`memory-${i}.png`,1000,800,c=>{
 c.fillStyle=['#524079','#27564F','#6E4337','#454875'][i-1];c.fillRect(0,0,1000,800);
 c.strokeStyle='#F6EFE3';c.lineWidth=3;c.beginPath();c.moveTo(0,50);c.quadraticCurveTo(500,260,1000,50);c.stroke();
 for(let j=0;j<10;j++)circle(c,50+j*100,70+Math.sin(j/9*Math.PI)*95,12,'#FFCC33');
 c.fillStyle='#F6EFE3';c.font='bold 54px Crew';c.fillText(['THE SOCIAL CLUB','GOOD COMPANY','ONE MORE ROUND','WHAT A NIGHT'][i-1],55,290);
 person(c,215,480,1.8,(i+1)%8);person(c,505,455,1.9,(i+3)%8);person(c,795,500,1.8,(i+5)%8);
});
