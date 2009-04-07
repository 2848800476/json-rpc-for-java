﻿{ data:null,
  SelectDiv:false, /* 下拉列表图层 */
  inputObj:null,   /* 存放value值输入对象 */
  descObj:null,    /* 存放描述的输入对象 */
  oFrom:null,      /* 计算图层宽度的对象 */
  oShdow:null,     /* 阴影图层对象 */
  getSlctObj:function(szId)
  {
    return (window.slctIptData || {})[szId]||{};
  }, setData:function(szId,a){this.getSlctObj("S" + szId)["collection"] = a;},
  getData:function(szId) /* 获取下拉列表数据 */
  {
    var rst = this.getSlctObj(szId)["collection"], i, s, o, k, key = "_id_";
    if(rst && 0 < rst.length && "" == rst[0][key].replace(/\d/g, ""))
    {
        for(i = 0; i < rst.length; i++)
        {
          s = [], o = rst[i];
          for(k in o)
             if(key != k)
                s.push(o[k]);
          rst[i][key] = s.join("\t");
        }
    }
    return this.data || this.getSlctObj(szId)["collection"] || []
  },scrollIntoView: function(c, el){
        if(!c)return this;
        var o = $(el), a = [parseInt(c.clientHeight, 10), parseInt(o.offset().top, 10), o.height(), parseInt(c.scrollTop, 10)];
        c.scrollTop = a[1] + a[2] - a[0];
        c.scrollTop = c.scrollTop;
    }, /* 高亮显示指定的行 */
  lightRow:function(n,flg,e)
  {
    var o = this.SelectDiv, tb = this.getByTagName("table",o), b = 0 < tb.length && 0 < tb[0].rows.length, r = b ? tb[0].rows : null;
    if(!b)return false;
    if(r.length > o["_lstNum"])
       r[o["_lstNum"] || 0].className='slcthand';
    if(0 > n)n = r.length - 1;
    if(r.length <= n)n = 0;
    r[n].className='cursor slctOver'; 
    if(!flg && 0 <= n && r.length > n)this.scrollIntoView(this.p(tb[0], "DIV"), r[n]);/*  r[n].scrollIntoView(true)*/
    o["_lstNum"] = n;
    if(3 == arguments.length)
      return this.stopPropagation(e),this.preventDefault(e), false;
    return n;
  },
  getSelectDataStr:function(oE, w)
  {
    var _t = this, a = this.getData(oE.id), a1 = ["<div class=\"cursor selectInput_FloatDiv\"><table cellPadding=\"0\" border=\"0\" class=\"xuiTable\" cellSpacing=\"0\" style=\"border:0px;width:100%;margin:0px;padding:0px;position: relative;left:0;top:0\">"], i, j, o, k,
        b = this.getSlctObj(oE.id)["displayFields"], bDisp = !b, key = "_id_";
    !bDisp && (b = b.split(/[,;\|\/]/));
    for(i = 0; i < a.length; i++)
    {
      o = a[i];
      a1.push("<tr");
      a1.push(" onclick=\"Select.onSelect(event, this)\" class=\"cursor\" onmouseover=\"return Select.lightRow(this.rowIndex,true,event)\"\">");
      if(bDisp)
      {
          for(k in o)
           if(key != k)
             a1.push("<td><nobr>"), a1.push(o[k]), a1.push("</nobr></td>");
      }
      else
      {
        for(j = 0; j < b.length; j++)
          a1.push("<td><nobr>"), a1.push(o[b[j]]), a1.push("</nobr></td>");
      }
      a1.push("</tr>");
    }
    a1.push("</table></div>");
    return a1.join("")
  }, /* 给对象设置value */
  setValueX:function(s, n,e)
  {
     var descObj = this.descObj, inputObj = this.inputObj;
     if(1 == n && descObj)descObj.value = s;
     else if(2 == n && inputObj)inputObj.value = s;
     else if(descObj && inputObj)
        inputObj.value = descObj.value = s;
     if(e)this.preventDefault(e), this.stopPropagation(e);
     return this;
  },/* 通过描述得到value */
  getValueByDesc:function(s)
  {
     var oT = this.getSlctObj(this.descObj.id), a = oT["collection"], i, b = (oT['valueField'] || "").split(/[,; ]/), b2 = 1 < b.length;
     /* 指定了两个字段:value，和描述字段 */
     if(oT['valueField'])
     {
         if(b2)
	     for(i = 0; i < a.length; i++)
	     {
	        if(s == a[i][b[1]])
	          return a[i][b[0]];
	     }
	     for(i = 0; i < a.length; i++)
	     {
	        if(-1 < a[i][b[0]].indexOf(s))
	          return a[i][b[0]];
	     }	     
     }
     return null;
  }, /* 选择的处理 */
  onSelect:function(e, oTr)
  {
     var o = this.SelectDiv, id = o.id, oIpt = o[id] && this.getDom(o[id]) || null,a,
         n = "number" == typeof oTr.rowIndex ? oTr.rowIndex : oTr, oT = this.getSlctObj(oIpt.id) || {},
         dt = this.getData(oIpt.id) || [], cbk = oT['selectCallBack'];
     if(0 <= n && dt.length > n)
     {
       /* 处理选择 */
       if(oT['valueField'])
       { /* value处理 */
         a = (oT['valueField'] || "").split(/[,; ]/);
         this.setValueX(dt[n][a[0]], 2, e);
         oIpt.value = (1 < a.length ? dt[n][a[1]] : dt[n][a[0]]);
       } /* 回调处理 */
       cbk && new Function("dt", "n", "oIpt", cbk +"(dt[n], oIpt);")(dt, n, oIpt);
       o["_lstNum"] = n;
     }else o["_over"] = 1;
     this.hidden(e);
     this.delInvalid(oIpt);
     if(e)this.preventDefault(e), this.stopPropagation(e);
  }, /* 检查当前输入对象的显示图层是否正在显示 */
  isShow: function(e, obj, oE)
  {
     var o = this.SelectDiv, szId = o.id;
     return(o && "block" == o.style.display && o[szId] == oE.id);
  },
  hidden: function()
  {
     this.hiddenShadow(this.getDom("_Xui_SelectDiv"));
     this.updata((this.descObj || {}).value || "");
     this.descObj["xuiBlur"] && this.descObj["xuiBlur"]();
  }, /* 更新过滤后的data数据 */
  updata:function(s)
  {
    if(!this.descObj)return this;
    if(0 == s.length)return this.data = null, this;
    var n, id = this.descObj.id, b = [], c = [], a = (this.getData(id), this.getSlctObj(id)["collection"]);
    if(!a || 0 == a.length)return 0;
    for(n = 0; n < a.length; n++)
      if(-1 < a[n]["_id_"].indexOf(s))
         b.push(a[n]);
      else c.push(a[n]);
    this.data = b.concat(c);
    return b.length;
  },/* 显示图层 */
  show: function()
  { 
   	 var o = this.SelectDiv, obj = this.getSlctObj(this.descObj.id);
   	 if(null == obj.displayWidth)obj.displayWidth = o.style.width;
   	 if(0 < this.getData(this.descObj.id).length)
       this.showDiv(this.p(this.descObj, "DIV"), this.SelectDiv, 
         parseInt(obj.displayWidth, 10), parseInt(o.style.height, 10));
     (o = $(o)).css({overflowY:'auto'});
     if(170 > o.attr('scrollHeight'))o.css({overflowY:'visible'});
  }, /* 检索过滤处理 */
  onInput:function(e, oIpt)
  {
     var _t = this;
     return this.RunOne(function()
     {
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
	       if(0 < n)
	          this.delInvalid(oIpt), _t.showSelectDiv(e, {width: o.style.width}, oIpt, _t.data);
	       else s && !oT["allowEdit"] && this.addInvalid(oIpt);
       }
       if(_t.isIE)
	   {
	       _t.addEvent(oIpt, "propertychange",  (_t[oIpt.id] || (_t[oIpt.id] = {})).onpropertychange = function(e)
	       {
	          _t.onInput.call(_t, e, oIpt);
	       });
	   }
     }, Select);
  }, /* 键盘事件处理 */
  onkeydown:function(e, oIpt)
  {
     e = e || window.event;
     var n = e.which || e.keyCode, o = this.SelectDiv, oT = this.getSlctObj(oIpt.id), i = o["_lstNum"] || 0;
     switch(n)
     {
        /* 接受连续退格键 e.repeat, 8 */
        /*Esc 关闭图层*/
        case 27:this.hidden(e);break;
        /* 回车选择 */
        case 13:
           this.onSelect(e, i);
           this.bIE ? (e.keyCode = 9) : '';
           this.hidden(e);
           break;
        case 38: /* 上 */
           i = this.lightRow(i - 1);
           this.stopPropagation(e),this.preventDefault(e);
           return false;
        case 40: /* 下 */
           i = this.lightRow(i + 1);
           this.stopPropagation(e),this.preventDefault(e);
           return false;
        default:;
     }
     return true;
  }, /* 显示下拉列表图层 */
  showSelectDiv: function(e, obj, oE)
  {
    var b3 = (3 == arguments.length), _t = this;
    e = e || window.event;
    return this.RunOne(function(){
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
       this.SelectDiv = o = this.createDiv({id:"_Xui_SelectDiv"});
       this.addEvent(o, "mousemove", fns).addEvent(o, "mousedown", fns)
           .addEvent(o, "scroll", fns)
           .addEvent(o, "mouseup", fns).addEvent(o, "mouseout", _t.bind(_t.hiddenSelectDiv));
       this.oShdow = this.getDom("xuiSelectShdow");
    }
    szId = o.id;
    /* 状态的处理: 输入对象的id保留 */
    o[szId] = oE.id, o["_lstNum"] = 0, o["_blur_"]= false, fns();

    /* 修正显示图层的上下位置 */
    /* if(190 < p.top - document.documentElement.scrollTop)p.top =  p.top - (o.clientHeight || 170) - h;*/
    /* 失去焦点就隐藏 */
    if(!oE[szId])
    {
       oE[szId] = o.id,
       this.addEvent(oE, "blur", function()
           {  /* 隐藏输入对象为空 */
              if(!_t.inputObj.value)
              { /* 允许输入新值就将描述输入对象的值赋予它，否则就设置空值 */
                if(_t.getSlctObj(_t.descObj.id)['allowEdit']) _t.inputObj.value = _t.descObj.value;
                else _t.descObj.value = '', _t.delInvalid(_t.descObj);
              }
              
              o["_blur_"]=true,_t.hiddenSelectDiv()
           })
           .addEvent(oE, "mousemove", function(e)
               {
                 o["tmer"] && _t.clearTimer(o["tmer"]),
                 _t.updata(oE.value),
                 _t.fnMvIstPoint(oE, oE.value.length, oE.value.length, e);
               });
    }
    _t.updata(oE.value);
    o.style['height'] = Math.min(15 * _t.getData(oE.id).length, 170) + 'px';
    o.innerHTML = _t.getSelectDataStr(oE, w);
    var nTm = new Date().getTime();
    _t.show();
    this.lightRow(0);
    e && this.stopPropagation(e),this.preventDefault(e);
    });    
  }, /* 隐藏图层的方法 */
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