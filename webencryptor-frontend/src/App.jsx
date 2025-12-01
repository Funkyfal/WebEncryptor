// src/App.jsx
import React, { useState } from 'react';
import TopTabs from './components/TopTabs';
import CryptoPanel from './CryptoPanel';
import HashPanel from './components/HashPanel'; // у тебя уже есть HashPanel в ./components

export default function App() {
    const [active, setActive] = useState('crypto');
    return (
        <div style={{padding:20, fontFamily: 'Inter, system-ui, sans-serif'}}>
            <h1>WebEncryptor</h1>
            <TopTabs active={active} setActive={setActive} />
            {active === 'crypto' && <CryptoPanel />}
            {active === 'hashmac' && <HashPanel />}
        </div>
    );
}
