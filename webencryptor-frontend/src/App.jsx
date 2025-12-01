import React from 'react'
import HashPanel from './components/HashPanel'
import FileEncryptPanel from './components/FileEncryptPanel'

export default function App() {
    return (
        <div style={{ padding: 20, fontFamily: 'Inter, system-ui, sans-serif' }}>
            <h1>WebEncryptor — demo UI</h1>
            <HashPanel />
            <hr />
            <FileEncryptPanel />
        </div>
    )
}
