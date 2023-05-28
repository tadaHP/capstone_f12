import "@uiw/react-md-editor/markdown-editor.css"
import "@uiw/react-markdown-preview/markdown.css"
import React, {useState} from 'react';
import dynamic from "next/dynamic";
import {handleInput} from "@/components/auth/common";
import PostWriteSetting from "@/components/post/PostWriteSetting";
import {Button, Input, Space} from "antd";
import Link from "next/link";

const PostEditor = dynamic(
  () => import("@/components/post/PostWrite"),
  {ssr: false}
);


const Write = () => {
  const [title, onChangeTitle] = handleInput("");
  const [content, setContent] = useState("**Hello Test World!**");
  const [isDoneWrite, setIsDoneWrite] = useState(false);

  const isDoneWritePost = (prev) => {
    setIsDoneWrite((prev) => !prev);
  }

  const editContent = (content) => {
    setContent(content);
  }


  return (
    isDoneWrite ? <PostWriteSetting title={title} content={content} isDoneWritePost={isDoneWritePost}/> :
      <div style={{display: "flex", flexDirection: "column", margin: "30px"}}>
        <Input bordered={false} value={title} placeholder="제목을 입력하세요" onChange={onChangeTitle} style={{fontSize: "3rem"}}></Input>
        <PostEditor content={content} editContent={editContent}/>
        <div className="space-align-block" style={{width: "50%"}}>
          <Space align="center" style={{display: "flex", justifyContent: "space-between"}}>
            <Link href={"./"}><Button>나가기</Button></Link>
            <div>
              <Button type="primary" style={{margin: "10px"}}>임시저장</Button>
              <Button type="primary" onClick={isDoneWritePost}>출간하기</Button>
            </div>
          </Space>
        </div>
      </div>
  );


};

export default Write;