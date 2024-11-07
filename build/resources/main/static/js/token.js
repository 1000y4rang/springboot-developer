const token = searchParam('token');

if(token){
    // 브라우저나 OS가 재시작하더라도 데이터가 파기되지 않습니다.
    localStorage.setItem("access_token", token);
}

function searchParam(key){
    return new URLSearchParams(location.search).get(key);
}