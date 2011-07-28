// iactivexDoc.cpp : implementation of the CiactivexDoc class
//

#include "stdafx.h"
#include "iactivex.h"

#include "iactivexDoc.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CiactivexDoc

IMPLEMENT_DYNCREATE(CiactivexDoc, CDocument)

BEGIN_MESSAGE_MAP(CiactivexDoc, CDocument)
END_MESSAGE_MAP()


// CiactivexDoc construction/destruction

CiactivexDoc::CiactivexDoc()
{
	// TODO: add one-time construction code here

}

CiactivexDoc::~CiactivexDoc()
{
}

BOOL CiactivexDoc::OnNewDocument()
{
	if (!CDocument::OnNewDocument())
		return FALSE;

	// TODO: add reinitialization code here
	// (SDI documents will reuse this document)

	return TRUE;
}




// CiactivexDoc serialization

void CiactivexDoc::Serialize(CArchive& ar)
{
	if (ar.IsStoring())
	{
		// TODO: add storing code here
	}
	else
	{
		// TODO: add loading code here
	}
}


// CiactivexDoc diagnostics

#ifdef _DEBUG
void CiactivexDoc::AssertValid() const
{
	CDocument::AssertValid();
}

void CiactivexDoc::Dump(CDumpContext& dc) const
{
	CDocument::Dump(dc);
}
#endif //_DEBUG


// CiactivexDoc commands
