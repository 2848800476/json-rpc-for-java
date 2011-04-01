﻿{ data:null,
  SelectDiv:false, /* 下拉列表图层 */
  inputObj:null,   /* 存放value值输入对象 */
  descObj:null,    /* 存放描述的输入对象 */
  oFrom:null,      /* 计算图层宽度的对象 */
  oShdow:null,     /* 阴影图层对象 */
  bFltFlg: false, /* 过滤的标志 */
  xfcSlctDivId: '_Xui_SelectDiv',
  upi4ajx:function(){
     if(Select.descObj && Select.descObj.id)this.descObj = Select.descObj = document.getElementById(Select.descObj.id);
     if(this.descObj && this.descObj.id)this.descObj = Select.descObj = document.getElementById(this.descObj.id);
     this.inputObj = Select.inputObj = $(this.descObj).next()[0];/* 下一行是2011-3-8新增加的，尝试解决getData最后一行的问题，未测试 */
  },
  getSlctObj:function(szId)
  {
    return (window.slctIptData || {})[szId]||{};
  }, setData:function(szId,a){this.getSlctObj("S" + szId)["collection"] = a;},
  getData:function(szId) /* 获取下拉列表数据 */
  {
    var rst = this.getSlctObj(szId)["collection"], i, s, o, k, key = "_id";
    if(rst && 0 < rst.length && rst[0] && (!rst[0][key] || "" == rst[0][key].replace(/\d/g, "")))
    {
        for(i = 0; i < rst.length; i++)
        {
          s = [], o = rst[i];
          for(k in o)
             if(key != k)
                s.push(o[k]);
          if(0 < s.length)
          rst[i][key] = s.join(",").toLowerCase();
        }
    }
    if(this.data && 0 == this.data.length)this.data = null;
    var oTmp09 = this.getSlctObj(szId);
    /* oTmp09["collection"]优先，则异步下拉列表没有问题，否则中文首字母定位存在问题 */
    if(fnSetSelectAjax[szId.substring(1)])return (oTmp09["collection"] || this.data || []);
    return (this.data || oTmp09["collection"] || [])
  }, /* 高亮显示指定的行 */
  lightRow:function(n,flg,e)
  {
    var o = this.SelectDiv, tb = this.getByTagName("table",o), b = 0 < tb.length && 0 < tb[0].rows.length, r = b ? tb[0].rows : null;
    /*o["tmer"] && this.clearTimer(o["tmer"]);*/
    if(!b || !o)return  false;
    if(r.length > o["_lstNum"])
       r[0 <= o["_lstNum"]? o["_lstNum"] : 0].className='slcthand';
    if(0 > n)n = r.length - 1;
    if(r.length <= n)n = 0;
    r[n].className='cursor slctOver'; 
    if(!flg && 0 <= n && r.length > n)this.fnSciv(this.SelectDiv, r[n]);
    o["_lstNum"] = n;
    if(3 == arguments.length)
      return this.stopPropagation(e),this.preventDefault(e), false;
    return n;
  },
  getSelectDataStr:function(oE, w, data)
  {
    var _t = Select, a = data || _t.getData(oE.id), a1 = ["<div class=\"cursor selectInput_FloatDiv\"><table cellPadding=0 border=0 class=xuiTable cellSpacing=0 style=\"border:0px;width:100%;margin:0px;padding:0px;position: relative;left:0;top:0\">"], i, j, o, k,
        b = _t.getSlctObj(oE.id)["displayFields"], bDisp = !b, key = "_id_";
     if(bDisp)
        b = window['slctIptData']["S" + oE.id]["displayFields"], bDisp = !b;
    !bDisp && (b = b.split(/[,;\|\/]/));
    if(!a ||!a.length || 0 == a.length)return "";
    for(i = 0; i < a.length; i++)
    {
      o = a[i];
      a1.push("<tr data='" + $.toJSON(o) + "' class=cursor");<%if(Const.noLockPage){%>
      if(1 == i % 2)a1.push(" style=\"background-color:<%=Const.slctRowBgColor2%>\"");
      <%}%>a1.push(" onclick=\"Select.onSelect(event, this)\" onmouseover=\"return Select.lightRow(this.rowIndex,true,event)\"\">");
      var szK1 = String((_t.descObj || oE || {value:''}).value || "").trim().replace(/([()\|\$\.\\])/g,"\\\1"), reg9 = new RegExp("(" + szK1 + ")", "gmi"), fnTrpc = function(s89){
         if(0 == szK1.length)return s89;
         s89 || (s89 = "");
         return s89.replace(reg9, "<b>$1</b>");      
      };
      if(bDisp)
      {
          for(k in o)
           if(key != k && "_id" != k && "function" != typeof o[k])
           {
             a1.push("<td><nobr>"), a1.push(fnTrpc(o[k])), a1.push(" </nobr></td>");
           }
      }
      else if(o)
      {
        for(j = 0; j < b.length; j++)
          if(key != b[j] && "_id" != b[j])
          {
             a1.push("<td><nobr>"), a1.push(fnTrpc(o[b[j]])), a1.push(" </nobr></td>");
          }
      }
      a1.push("</tr>");
    }
    a1.push("</table></div>");
    var s = a1.join("");
    _t.bHvRplc = false;
    return s;
  }, /* 给对象设置value */
  setValueX:function(s, n,e)
  {
     Select.upi4ajx();
     var _t = this, descObj = this.descObj, inputObj = this.inputObj, bEdit = "true" == _t.getSlctObj(_t.descObj.id)['allowEdit'];
     if(1 == n && descObj)descObj.value = s;
     else if(2 == n && inputObj)inputObj.value = s;
     else if(descObj && inputObj)
        inputObj.value = descObj.value = s;
      if("" == _t.descObj.value && !bEdit)_t.inputObj.value = "";
     if(e)this.preventDefault(e), this.stopPropagation(e);<%if(Const.noLockPage){%>
     if(0 == String(descObj.value).trim().length)descObj.value = "<%=Const.pleaseSelect%>";
     <%}%>return this;
  },/* 通过value获得描述 */
  getDescByValue:function(s,o){
      o || (o = this.descObj.id);
      if("string" != typeof o)o = o.id;
      if("undefined" == typeof window['slctIptData'])return "";
      var obj = window['slctIptData'][o],a = obj.valueField.split(/[,;\|\s]/), d = a[a.length - 1], i, v = a[0];
      a = obj.collection;
      if(a)
      for(i = a.length; 0 <= --i;)
      {
          if(a[i][v] == s)
          {
              return a[i][d];
          }
      }
      return s;
  },/* 通过描述得到value */
  getValueByDesc:function(s)
  {
     var oT = this.getSlctObj(this.descObj.id), a = oT["collection"], i, b = (oT['valueField'] || "").split(/[,; ]/), b2 = 1 < b.length;
     /* 指定了两个字段:value，和描述字段 */
     if(oT['valueField'])
     {
         if(b2 && a)
	     for(i = 0; i < a.length; i++)
	     {
	        if(a[i] && s == a[i][b[1]])
	          return a[i][b[0]];
	     }
	     if(a)
	     for(i = 0; i < a.length; i++)
	     {
	        if(a[i] && a[i][b[0]] && -1 < String(a[i][b[0]]).indexOf(s))
	          return a[i][b[0]];
	     }
     }
     return null;
  }, /* 选择的处理 */
  onSelect:function(e, oTr)
  {
     Select.upi4ajx();
     var rData = null, o = this.SelectDiv, id = o.id, oIpt = o[id] && this.getDom(o[id]) || null,a,
         n = "number" == typeof oTr.rowIndex ? oTr.rowIndex : oTr, oT = this.getSlctObj(oIpt.id) || {},
         dt = this.getData(oIpt.id) || [], cbk = oT['selectCallBack'];
     try{rData = eval("1," + oTr.data);}catch(e){}
     if(0 <= n && dt.length > n)
     {
       if(rData)
       for(var kr in rData)dt[n][kr] = rData[kr];
       /* 处理选择 */
       if(oT['valueField'])
       { /* value处理 */
         a = (oT['valueField'] || "").split(/[,; ]/);
         oIpt.value = (1 < a.length ? dt[n][a[1]] : dt[n][a[0]]);
         this.setValueX(dt[n][a[0]], 2, e);
       } /* 回调处理 */
       cbk && new Function("dt", "n", "oIpt", cbk +"(dt[n], oIpt,n);")(dt, n, oIpt);
       o["_lstNum"] = n;
     }else o["_over"] = 1;
     this.hidden(e);
     this.delInvalid(oIpt);
     if("click" == e.type) window.nxtfcs(e, Select.descObj);
     if(e)this.preventDefault(e), this.stopPropagation(e);
  }, /* 检查当前输入对象的显示图层是否正在显示 */
  isShow: function(e, obj, oE)
  {
     var o = this.SelectDiv, szId = o.id;
     return(o && "block" == o.style.display && o[szId] == oE.id);
  },
  hidden: function()
  {
     this.hiddenShadow(this.getDom(Select.xfcSlctDivId));
     this.updata((this.descObj || {}).value || "");
     this.descObj["xuiBlur"] && this.descObj["xuiBlur"]();
  }, /* 更新过滤后的data数据 */
  updata:function(s)
  {
    if(!this.descObj)return this;
    if(0 == s.length)return this.data = null, this;
    var n, id = this.descObj.id, b = [], w = [], c = [], a = (this.getData(id), this.getSlctObj(id)["collection"]);
    if(!a || 0 == a.length)return 0;
    for(n = 0; n < a.length; n++)
      if(a[n]){
      if(a[n]["_id"] && -1 < a[n]["_id"].indexOf(s.toLowerCase() +","))
         b.push(a[n]);
      else  if(a[n]["_id"] && -1 < a[n]["_id"].indexOf(s.toLowerCase()))
         w.push(a[n]);
      else c.push(a[n]);}
    this.data = b.concat(w);
    /*if(Select.bFltFlg)*/
    Select.data = this.data = this.data.concat(c),Select.bFltFlg = false;
    return b.length;
  },/* 显示图层 */
  show: function()
  { 
   	 var o = this.SelectDiv, obj = this.getSlctObj(this.descObj.id);
   	 if(null == obj.displayWidth)obj.displayWidth = obj.clientWidth;
   	 /*if(0 < this.getData(this.descObj.id).length) 没有数据也显示图层，表示是下拉只是没有数据 */
       this.showDiv(this.p(this.descObj, "DIV"), this.SelectDiv, 
         parseInt(obj.displayWidth, 10), 150);<%
  if(Const.noLockPage){
  %>this.fnResize();<%}
  %>},/* 使得中心fn的过程中不触发oninput */
	fnNoInput:function(fn){
	  var _t = this;
	   window.bBoBq = true;fn();setTimeout(function(){window.bBoBq = false},13);
	}, /* 检索过滤处理 */
  onInput:function(e, oIpt)
  {
     var _t = this;
     if(window.bBoBq)return false;
     _t.fnNoInput(function(){
	       _t.stopPropagation(e),_t.preventDefault(e);
	       if(oIpt.readOnly || oIpt.disabled)return false;
	       if(_t.isIE)
	       {
	         _t.descObj = oIpt;
	         _t.detachEvent(oIpt, "propertychange", _t[oIpt.id] && _t[oIpt.id].onpropertychange || oIpt["onpropertychange"] || function(){});
	         oIpt["onpropertychange"] = null;
	       }
	       _t.getData(oIpt.id);
	       var n = 0, o = _t.SelectDiv, oT = _t.getSlctObj(oIpt.id),
	           s = oIpt.value.replace(/(^\s+)|(\s+$)/g, "");
	       if(o)
	       {
		       /* 检索、过滤处理，并返回过滤得到的结果条数 */
		       n = _t.updata(s); /* _t.getData(oIpt.id); */
		       /* 从码表中尝试获取值，如果没有找到,如果允许编辑就用描述字段内容 */
		       s = _t.getValueByDesc(s) || oT["allowEdit"] && s || "";
		       _t.setValueX(s, 2, e);
		       /* if(oIpt.getAttribute("oldValue") != s || 0 == n)_t.setValueX("", 2, e); */
		          _t.delInvalid(oIpt), _t.showSelectDiv(e, {width: o.style.width}, oIpt, _t.data);
	       }
	       if(_t.isIE)
		   {
		       _t.addEvent(oIpt, "propertychange",  (_t[oIpt.id] || (_t[oIpt.id] = {})).onpropertychange = function(e)
		       {
		          _t.onInput.call(_t, e, oIpt);
		       });
		   }
	   });
  }, /* 键盘事件处理 */
  onkeydown:function(e, oIpt)
  {
     /*Select.bFltFlg = true;*/
     if(oIpt.readOnly || oIpt.disabled)return false;
     e = e || window.event;
     Select.upi4ajx();
     var n = e.which || e.keyCode, o = this.SelectDiv, oT = this.getSlctObj(oIpt.id), i = o["_lstNum"] || 0;
     if(-1 == o["_lstNum"])o["_lstNum"] = 0, i = -1;
     switch(n)
     {
        /* 接受连续退格键 e.repeat, 8 */
        /*Esc 关闭图层*/
        case 27:this.hidden(e);break;
        /* 回车选择 */
        case 13:
           this.onSelect(e, i);
           this.hidden(e);
           this.bIE ? (e.keyCode = 9) : '';
           window.nxtfcs(e, this.descObj);
           break;
        case 38: /* 上 */
           i = this.lightRow(i - 1);
           this.stopPropagation(e),this.preventDefault(e);
           return false;
        case 40: /* 下 */
           this.show();
           i = this.lightRow(i + 1);
           this.stopPropagation(e),this.preventDefault(e);
           return false;
        default:;
     }
     return true;
  }, /* 显示下拉列表图层 */
  showSelectDiv: function(e, obj, oE)
  {
   if(oE.readOnly || oE.disabled)return false;
    var b3 = (3 == arguments.length), _t = this;
    e = e || window.event;
    return this.RunOne(function(){
       _t.data = [];
      if(0 == _t.getData(oE.id).length)$('#' + Select.xfcSlctDivId).hide();
      if(oE.readOnly || oE.disabled || (this.isShow(e, obj, oE) && b3))return false;
      var o = this.SelectDiv, szId, oTable = (this.oFrom = this.p(oE,"TABLE")),
        oR = this.getOffset(oE),h = oR[3], w = parseInt((obj||{}).width || $(oE.parentNode).width()),
        k,
        fns = _t.bind(function()
        {
          var o = this.SelectDiv;
          if(0 < (this.getData(oE.id) || []).length)
          {
	          o["tmer"] && _t.clearTimer(o["tmer"]);
              if(o.style.height)
                 this.show();
          }
          o["_in_"] = true
        });
    /* 输入对象 */
    _t.inputObj = (_t.descObj = oE).parentNode.getElementsByTagName("input")[1];
    if(!o)
    {
       this.SelectDiv = o = this.createDiv({id:Select.xfcSlctDivId});
       this.addEvent(o, "mousedown", fns)
           .addEvent(o, "scroll", fns)
           .addEvent(o, "mouseup", fns).addEvent(o, "mouseout", _t.bind(_t.hiddenSelectDiv));
       this.oShdow = this.getDom("xuiSelectShdow");
    }
    szId = o.id;
    /* 状态的处理: 输入对象的id保留 */
    o[szId] = oE.id, o["_lstNum"] = -1, o["_blur_"]= false, fns();

    /* 修正显示图层的上下位置 */
    /* if(190 < p.top - document.documentElement.scrollTop)p.top =  p.top - (o.clientHeight || 170) - h;*/
    /* 失去焦点就隐藏 */
    if(!oE[szId])
    {
       oE[szId] = o.id,
       this.addEvent(oE, "blur", function()
           {  window.bBoBq = true;/* 隐藏输入对象为空 */
              var bEdit = "true"==_t.getSlctObj(_t.descObj.id)['allowEdit'] || window['slctIptData'][_t.descObj.id]["allowEdit"];
              /*if(false == bEdit)bEdit = "true" == window['slctIptData'][_t.descObj.id]["allowEdit"];*/
              if(!_t.inputObj.value)
              { /* 允许输入新值就将描述输入对象的值赋予它，否则就设置空值 */
                 if(bEdit) _t.inputObj.value = _t.descObj.value;
                else if(_t.descObj.value)_t.descObj.value = '';
              }
              else
              {
                 if(!_t.getValueByDesc(_t.descObj.value) &&  !bEdit)
                    _t.descObj.value = _t.inputObj.value = '';
              }
              if("" == _t.descObj.value && !bEdit)_t.inputObj.value = "";
              o["_blur_"]=true,_t.hiddenSelectDiv();<%
              if(Const.noLockPage){
                  %>if(0 == String(_t.descObj.value).trim().length)_t.descObj.value = "<%=Const.pleaseSelect%>";<%
              }%>window.bBoBq = false;
           });
    }
    _t.updata(oE.value);
     o.innerHTML=_t.getSelectDataStr(oE,w)
     <%if(Const.noLockPage){%>if(o.innerHTML){<%}%>
    if(this.bHvRplc)this.lightRow(this.SelectDiv["_lstNum"]=0);
    var nTm=new Date().getTime();
    window.hdAll.start();
    _t.show();
    /*高度计算*/<%if(Const.noLockPage){%>}<%}%>
    e && this.stopPropagation(e),this.preventDefault(e);
    });    
  },
   fnResize:function(){<%
  if(Const.noLockPage){
  %>var oDiv = $("#_Xui_SelectDiv"), oT = $(oDiv.find("table")[0]), n;
  if((n = oT.height()) < parseInt('<%=Const.slctHeight%>'))oDiv.height(n + 'px');
  <%
  }
  %>},/* 隐藏图层的方法 */
  hiddenSelectDiv:function()
  {
    var _t = Select, o = _t.SelectDiv;
    o["_tm_"] = new Date().getTime();
    o["_in_"] = false;
    /* 注册自动关闭,防止重入，如果重入就回启动多个timer服务定时器 */
    o["tmer"] = _t.regTimer(function(e)
    {
       if(o["_blur_"])
       {
	       if(o["_in_"])return true;
	       if(333 < new Date().getTime() - o["_tm_"])
	          return _t.hidden(e), true;
       }
       return false
    }, 333);
  }
}