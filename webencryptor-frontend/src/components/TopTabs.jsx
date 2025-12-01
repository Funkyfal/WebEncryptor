// src/components/TopTabs.jsx
import React from 'react';

export default function TopTabs({active, setActive}) {
    return (
        <div style={{display:'flex', gap:10, marginBottom:20}}>
            <button onClick={()=>setActive('crypto')} style={{fontWeight: active==='crypto' ? '700' : '400'}}>Crypto</button>
            <button onClick={()=>setActive('hashmac')} style={{fontWeight: active==='hashmac' ? '700' : '400'}}>Hash & MAC</button>
        </div>
    );
}
